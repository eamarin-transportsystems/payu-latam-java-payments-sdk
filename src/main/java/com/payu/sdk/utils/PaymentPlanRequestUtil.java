/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 developers-payu-latam
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.payu.sdk.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.payu.sdk.PayU;
import com.payu.sdk.constants.Constants;
import com.payu.sdk.enums.SubscriptionCreationSource;
import com.payu.sdk.exceptions.InvalidParametersException;
import com.payu.sdk.exceptions.PayUException;
import com.payu.sdk.model.AdditionalValue;
import com.payu.sdk.model.Address;
import com.payu.sdk.model.Currency;
import com.payu.sdk.model.Language;
import com.payu.sdk.model.PaymentMethodType;
import com.payu.sdk.model.request.Request;
import com.payu.sdk.paymentplan.model.BankAccount;
import com.payu.sdk.paymentplan.model.Customer;
import com.payu.sdk.paymentplan.model.PaymentPlanCreditCard;
import com.payu.sdk.paymentplan.model.RecurringBill;
import com.payu.sdk.paymentplan.model.RecurringBillItem;
import com.payu.sdk.paymentplan.model.Subscription;
import com.payu.sdk.paymentplan.model.SubscriptionPlan;
import com.payu.sdk.payments.model.BankAccountListRequest;
import com.payu.sdk.payments.model.CustomerListRequest;
import com.payu.sdk.payments.model.PaymentPlanCreditCardListRequest;
import com.payu.sdk.payments.model.RecurringBillItemListRequest;
import com.payu.sdk.payments.model.RecurringBillListRequest;
import com.payu.sdk.payments.model.SubscriptionPlanListRequest;
import com.payu.sdk.payments.model.SubscriptionsListRequest;

/**
 * Utility for payment plan requests in the PayU SDK.
 *
 * @author PayU Latam
 * @since 1.0.0
 * @version 1.0.0, 11/09/2013
 */
public final class PaymentPlanRequestUtil extends CommonRequestUtil {

	/**
	 * Private Constructor
	 */
	private PaymentPlanRequestUtil() {
	}

	// valid keys for CreditCard
	public static final String[] CREDIT_CARD_VALID_PARAMS = new String[] {
			PayU.PARAMETERS.TOKEN_ID,
			PayU.PARAMETERS.CREDIT_CARD_NUMBER,
			PayU.PARAMETERS.CREDIT_CARD_EXPIRATION_DATE,
			PayU.PARAMETERS.PAYMENT_METHOD, PayU.PARAMETERS.PAYER_NAME,
			PayU.PARAMETERS.PAYER_STREET, PayU.PARAMETERS.PAYER_STREET_2,
			PayU.PARAMETERS.PAYER_STREET_3, PayU.PARAMETERS.PAYER_CITY,
			PayU.PARAMETERS.PAYER_STATE, PayU.PARAMETERS.PAYER_COUNTRY,
			PayU.PARAMETERS.PAYER_POSTAL_CODE, PayU.PARAMETERS.PAYER_PHONE };

	// valid keys for BankAccount
	public static final String[] BANK_ACCOUNT_VALID_PARAMS = new String[] {
			PayU.PARAMETERS.BANK_ACCOUNT_ID,
			PayU.PARAMETERS.BANK_ACCOUNT_DOCUMENT_NUMBER,
			PayU.PARAMETERS.BANK_ACCOUNT_DOCUMENT_NUMBER_TYPE,
			PayU.PARAMETERS.BANK_ACCOUNT_CUSTOMER_NAME,
			PayU.PARAMETERS.BANK_ACCOUNT_AGENCY_NUMBER,
			PayU.PARAMETERS.BANK_ACCOUNT_AGENCY_DIGIT,
			PayU.PARAMETERS.BANK_ACCOUNT_ACCOUNT_DIGIT,
			PayU.PARAMETERS.BANK_ACCOUNT_NUMBER,
			PayU.PARAMETERS.BANK_ACCOUNT_BANK_NAME,
			PayU.PARAMETERS.BANK_ACCOUNT_TYPE,
			PayU.PARAMETERS.BANK_ACCOUNT_STATE };

	public final static String EMPTY = "";
	/* Build Methods */

	/**
	 * Sets the authentication credentials to the API request object.
	 *
	 * @param parameters the parameters.
	 * @param request the API request.
	 */
	private static void setAuthenticationCredentials(Map<String, String> parameters, Request request) {

		request.setApiLogin(getParameter(parameters, PayU.PARAMETERS.API_LOGIN));
		request.setApiKey(getParameter(parameters, PayU.PARAMETERS.API_KEY));

		String language = getParameter(parameters, PayU.PARAMETERS.LANGUAGE);
		if (language != null) {
			request.setLanguage(Language.valueOf(language));
		}
		else {
			request.setLanguage(null);
		}

		String isTest = getParameter(parameters, PayU.PARAMETERS.IS_TEST);
		if (isTest != null) {
			request.setTest(Boolean.getBoolean(getParameter(parameters, PayU.PARAMETERS.IS_TEST)));
		}
		else {
			request.setTest(false);
		}
	}

	/* PRIVATE METHODS */

	/**
	 * Build the plan additional values (currency and value)
	 *
	 * @param txCurrency
	 *            The currency of the plan
	 * @param txValue
	 *            The value of the plan
	 * @param txTax
	 *            The tax of the plan
	 * @param txTaxReturnBase
	 *            The tax return base of the plan
	 * @return The created list of additional values
	 */
	private static List<AdditionalValue> buildPlanAdditionalValues(
			Currency txCurrency, BigDecimal txValue, BigDecimal txTax,
			BigDecimal txTaxReturnBase) {

		if (txCurrency == null || txValue == null) {
			return null;
		}

		List<AdditionalValue> values = new ArrayList<AdditionalValue>();
		addAdditionalValue(txCurrency, Constants.PLAN_VALUE, txValue, values);
		addAdditionalValue(txCurrency, Constants.PLAN_TAX, txTax, values);
		addAdditionalValue(txCurrency, Constants.PLAN_TAX_RETURN_BASE,
				txTaxReturnBase, values);

		return values;
	}

	/**
	 * Build the item additional values (currency and value)
	 *
	 * @param txCurrency
	 *            The currency of the plan
	 * @param txValue
	 *            The value of the plan
	 * @param txTax
	 *            The tax of the plan
	 * @param txTaxReturnBase
	 *            The tax return base of the plan
	 * @return The created list of additional values
	 */
	private static List<AdditionalValue> buildItemAdditionalValues(
			Currency txCurrency, BigDecimal txValue, BigDecimal txTax,
			BigDecimal txTaxReturnBase) {

		if (txCurrency == null || txValue == null) {
			return null;
		}

		List<AdditionalValue> values = new ArrayList<AdditionalValue>();
		addAdditionalValue(txCurrency, Constants.ITEM_VALUE, txValue, values);
		addAdditionalValue(txCurrency, Constants.ITEM_TAX, txTax, values);
		addAdditionalValue(txCurrency, Constants.ITEM_TAX_RETURN_BASE,
				txTaxReturnBase, values);

		return values;
	}

	/**
	 * Creates or updates an additional value
	 *
	 * @param txCurrency
	 *            The transaction currency
	 * @param name
	 *            The additional value name
	 * @param value
	 *            The additional value value
	 * @param additionalValues
	 *            The additional values list
	 */
	private static void addAdditionalValue(Currency txCurrency, String name,
			BigDecimal value, List<AdditionalValue> additionalValues) {

		if (value != null) {
			AdditionalValue additionalValue = new AdditionalValue();
			additionalValue.setName(name);
			additionalValue.setCurrency(txCurrency);
			additionalValue.setValue(value);

			additionalValues.add(additionalValue);
		}
	}

	/* PUBLIC METHODS */

	/**
	 * Builds a costumer request
	 *
	 * @param parameters
	 *            The parameters to be sent to the server
	 * @return The complete remove credit card token request
	 */
	public static Customer buildCustomerRequest(Map<String, String> parameters) {


		String customerName = getParameter(parameters,
				PayU.PARAMETERS.CUSTOMER_NAME);

		String customerEmail = getParameter(parameters,
				PayU.PARAMETERS.CUSTOMER_EMAIL);

		String customerId = getParameter(parameters,
				PayU.PARAMETERS.CUSTOMER_ID);

		Customer request = new Customer();
		setAuthenticationCredentials(parameters, request);

		request.setFullName(customerName);
		request.setEmail(customerEmail);
		request.setId(customerId);

		return request;
	}

	/**
	 * Builds a costumer request
	 *
	 * @param parameters
	 *            The parameters to be sent to the server
	 * @return The request by customerRequestList
	 * @throws PayUException
	 */
	public static CustomerListRequest buildCustomerListRequest(Map<String, String> parameters) throws PayUException {

		Map<String, String> parametersFilter=new HashMap<String, String>();

		parametersFilter.put(PayU.PARAMETERS.PLAN_ID,
				parameters.get(PayU.PARAMETERS.PLAN_ID));

		parametersFilter.put(PayU.PARAMETERS.PLAN_CODE,
				parameters.get(PayU.PARAMETERS.PLAN_CODE));

		parametersFilter.put(PayU.PARAMETERS.LIMIT,
				parameters.get(PayU.PARAMETERS.LIMIT));

		parametersFilter.put(PayU.PARAMETERS.OFFSET,
				parameters.get(PayU.PARAMETERS.OFFSET));

		CustomerListRequest request = new CustomerListRequest();
		setAuthenticationCredentials(parameters, request);
		request.setMap(parametersFilter);

		return request;
	}

	/**
	 * Builds a credit card request
	 *
	 * @param parameters
	 *            The parameters to be sent to the server
	 * @return The complete credit card request
	 * @throws InvalidParametersException
	 */
	public static PaymentPlanCreditCard buildCreditCardRequest(
			Map<String, String> parameters) throws InvalidParametersException {

		String tokenId = getParameter(parameters, PayU.PARAMETERS.TOKEN_ID);
		String customerId = getParameter(parameters,
				PayU.PARAMETERS.CUSTOMER_ID);
		String creditCardNumber = getParameter(parameters,
				PayU.PARAMETERS.CREDIT_CARD_NUMBER);
		String creditCardName = getParameter(parameters,
				PayU.PARAMETERS.PAYER_NAME);
		String type = getParameter(parameters, PayU.PARAMETERS.PAYMENT_METHOD);
		String line1 = getParameter(parameters, PayU.PARAMETERS.PAYER_STREET);
		String line2 = getParameter(parameters, PayU.PARAMETERS.PAYER_STREET_2);
		String line3 = getParameter(parameters, PayU.PARAMETERS.PAYER_STREET_3);
		String city = getParameter(parameters, PayU.PARAMETERS.PAYER_CITY);
		String state = getParameter(parameters, PayU.PARAMETERS.PAYER_STATE);
		String country = getParameter(parameters, PayU.PARAMETERS.PAYER_COUNTRY);
		String postalCode = getParameter(parameters,
				PayU.PARAMETERS.PAYER_POSTAL_CODE);
		String phone = getParameter(parameters, PayU.PARAMETERS.PAYER_PHONE);
		String expDate = getParameter(parameters,
				PayU.PARAMETERS.CREDIT_CARD_EXPIRATION_DATE);

		String document = getParameter(parameters,
				PayU.PARAMETERS.CREDIT_CARD_DOCUMENT);

		Address address = new Address();
		address.setLine1(line1 != null ? line1 : EMPTY);
		address.setLine2(line2 != null ? line2 : EMPTY);
		address.setLine3(line3 != null ? line3 : EMPTY);
		address.setCity(city != null ? city : EMPTY);
		address.setState(state);
		address.setCountry(country);
		address.setPostalCode(postalCode != null ? postalCode : EMPTY);
		address.setPhone(phone);

		PaymentPlanCreditCard request = new PaymentPlanCreditCard();
		setAuthenticationCredentials(parameters, request);

		request.setToken(tokenId);
		request.setCustomerId(customerId);
		request.setNumber(creditCardNumber);
		request.setName(creditCardName);
		request.setType(type);
		request.setAddress(address);
		request.setDocument(document != null ? document : EMPTY);

		validateDateParameter(expDate,
				PayU.PARAMETERS.CREDIT_CARD_EXPIRATION_DATE,
				Constants.SECUNDARY_DATE_FORMAT);

		if (expDate != null) {

			String[] date = expDate.split("/");

			request.setExpMonth(Integer.parseInt(date[1]));
			request.setExpYear(Integer.parseInt(date[0]));

		}

		return request;
	}

	/**
	 * Builds a credit card request
	 *
	 * @param parameters
	 *            The parameters to be sent to the server
	 * @return The complete credit card request
	 * @throws InvalidParametersException
	 */
	public static PaymentPlanCreditCardListRequest buildCreditCardListRequest(
			Map<String, String> parameters) throws InvalidParametersException {
		PaymentPlanCreditCardListRequest request = new PaymentPlanCreditCardListRequest();
		String customerId = getParameter(parameters,
				PayU.PARAMETERS.CUSTOMER_ID);
		request.setCustomerId(customerId);
		return request;
	}

	/**
	 * Builds a bank account request
	 *
	 * @param parameters The parameters to be sent to the server
	 * @return The complete bank account request
	 * @throws InvalidParametersException
	 */
	public static BankAccount buildBankAccountRequest(Map<String, String> parameters)
			throws InvalidParametersException {

		String bankAccountId = getParameter(parameters,
				PayU.PARAMETERS.BANK_ACCOUNT_ID);
		String customerId = getParameter(parameters,
				PayU.PARAMETERS.CUSTOMER_ID);
		String accountId = getParameter(parameters,
				PayU.PARAMETERS.ACCOUNT_ID);
		String customerName = getParameter(parameters,
				PayU.PARAMETERS.BANK_ACCOUNT_CUSTOMER_NAME);
		String documentNumber = getParameter(parameters,
				PayU.PARAMETERS.BANK_ACCOUNT_DOCUMENT_NUMBER);
		String documentNumberType = getParameter(parameters,
				PayU.PARAMETERS.BANK_ACCOUNT_DOCUMENT_NUMBER_TYPE);
		String bankName = getParameter(parameters,
				PayU.PARAMETERS.BANK_ACCOUNT_BANK_NAME);
		String bankType = getParameter(parameters,
				PayU.PARAMETERS.BANK_ACCOUNT_TYPE);
		String accountNumber = getParameter(parameters,
				PayU.PARAMETERS.BANK_ACCOUNT_NUMBER);
		String state = getParameter(parameters,
				PayU.PARAMETERS.BANK_ACCOUNT_STATE);
		String country = getParameter(parameters,
				PayU.PARAMETERS.COUNTRY);
		String accountDigit = getParameter(parameters,
				PayU.PARAMETERS.BANK_ACCOUNT_ACCOUNT_DIGIT);
		String agencyDigit = getParameter(parameters,
				PayU.PARAMETERS.BANK_ACCOUNT_AGENCY_DIGIT);
		String agencyNumber = getParameter(parameters,
				PayU.PARAMETERS.BANK_ACCOUNT_AGENCY_NUMBER);

		BankAccount request = new BankAccount();
		setAuthenticationCredentials(parameters, request);

		request.setId(bankAccountId);
	    request.setAccountId(accountId);
		request.setCustomerId(customerId);
		request.setName(customerName);
		request.setDocumentNumber(documentNumber);
		request.setDocumentNumberType(documentNumberType);
		request.setBank(bankName);
		request.setType(bankType);
		request.setAccountNumber(accountNumber);
		request.setState(state);
		request.setCountry(country);
		request.setAgencyDigit(agencyDigit);
		request.setAgencyNumber(agencyNumber);
		request.setAccountDigit(accountDigit);

		return request;
	}

	/**
	 * Builds a bank account list request
	 *
	 * @param parameters The parameters to be sent to the server
	 * @return The complete bank account request
	 * @throws InvalidParametersException
	 */
	public static BankAccountListRequest buildBankAccountListRequest(Map<String, String> parameters)
			throws InvalidParametersException {

		String customerId = getParameter(parameters,
				PayU.PARAMETERS.CUSTOMER_ID);

		BankAccountListRequest request = new BankAccountListRequest();
		setAuthenticationCredentials(parameters, request);

		request.setCustomerId(customerId);

		return request;
	}

	/**
	 * Builds a subscription plan request
	 *
	 * @param parameters
	 *            The parameters to be sent to the server
	 * @return The complete SubscriptionPlan request
	 * @throws InvalidParametersException
	 */
	public static SubscriptionPlan buildSubscriptionPlanRequest(
			Map<String, String> parameters) throws InvalidParametersException {

		String planCode = getParameter(parameters, PayU.PARAMETERS.PLAN_CODE);
		Integer accountId = getIntegerParameter(parameters,
				PayU.PARAMETERS.ACCOUNT_ID);
		String planDescription = getParameter(parameters,
				PayU.PARAMETERS.PLAN_DESCRIPTION);
		String planInterval = getParameter(parameters,
				PayU.PARAMETERS.PLAN_INTERVAL);
		Integer planIntervalCount = getIntegerParameter(parameters,
				PayU.PARAMETERS.PLAN_INTERVAL_COUNT);
		Integer planTrialPeriodDays = getIntegerParameter(parameters,
				PayU.PARAMETERS.PLAN_TRIAL_PERIOD_DAYS);
		Currency planCurrency = getEnumValueParameter(Currency.class,
				parameters, PayU.PARAMETERS.PLAN_CURRENCY);
		BigDecimal planValue = getBigDecimalParameter(parameters,
				PayU.PARAMETERS.PLAN_VALUE);
		BigDecimal planTax = getBigDecimalParameter(parameters,
				PayU.PARAMETERS.PLAN_TAX);
		BigDecimal planTaxReturnBase = getBigDecimalParameter(parameters,
				PayU.PARAMETERS.PLAN_TAX_RETURN_BASE);
		Integer maxPaymentsAllowed = getIntegerParameter(parameters,
				PayU.PARAMETERS.PLAN_MAX_PAYMENTS);
		Integer planPaymentAttemptsDelay = getIntegerParameter(parameters,
				PayU.PARAMETERS.PLAN_ATTEMPTS_DELAY);

		Integer planPaymentMaxPendingPayments = getIntegerParameter(parameters,
				PayU.PARAMETERS.PLAN_MAX_PENDING_PAYMENTS);
		Integer planPaymentMaxPaymentAttemps = getIntegerParameter(parameters,
				PayU.PARAMETERS.PLAN_MAX_PAYMENT_ATTEMPTS);

		SubscriptionPlan request = new SubscriptionPlan();
		setAuthenticationCredentials(parameters, request);

		request.setAccountId(accountId);
		request.setPlanCode(planCode);
		request.setDescription(planDescription);
		request.setInterval(planInterval);
		request.setIntervalCount(planIntervalCount);
		request.setTrialDays(planTrialPeriodDays);
		request.setMaxPaymentsAllowed(maxPaymentsAllowed);
		request.setAdditionalValues(buildPlanAdditionalValues(planCurrency,
				planValue, planTax, planTaxReturnBase));
		request.setPaymentAttemptsDelay(planPaymentAttemptsDelay);
		request.setMaxPaymentAttempts(planPaymentMaxPaymentAttemps);
		request.setMaxPendingPayments(planPaymentMaxPendingPayments);

		return request;
	}

	/**
	 * Builds a subscription plan list request
	 *
	 * @param parameters
	 *            The parameters to be sent to the server
	 * @return The complete SubscriptionPlan request
	 * @throws InvalidParametersException
	 * @throws PayUException
	 */
	public static SubscriptionPlanListRequest buildSubscriptionPlanListRequest(Map<String, String> parameters)
			throws InvalidParametersException, PayUException {

		SubscriptionPlanListRequest request = new SubscriptionPlanListRequest();
		request.setMap(parameters);
		setAuthenticationCredentials(parameters, request);

		return request;
	}

	/**
	 * Builds a Customer with a CreditCard request
	 *
	 * @param parameters
	 *            The parameters to be sent to the server
	 * @return The complete Customer with a CreditCard request
	 * @throws InvalidParametersException
	 */
	public static Customer buildCustomerWithCreditCardRequest(
			Map<String, String> parameters) throws InvalidParametersException {

		PaymentPlanCreditCard creditCard = buildCreditCardRequest(parameters);
		Customer request = buildCustomerRequest(parameters);
		setAuthenticationCredentials(parameters, request);

		request.addCreditCard(creditCard);

		return request;
	}


	/**
	 * Builds a Customer with a BankAccount request
	 *
	 * @param parameters
	 *            The parameters to be sent to the server
	 * @return The complete Customer with a CreditCard request
	 * @throws InvalidParametersException
	 */
	public static Customer buildCustomerWithBankAccountRequest(
			Map<String, String> parameters) throws InvalidParametersException {

		BankAccount bankAccount = buildBankAccountRequest(parameters);
		Customer request = buildCustomerRequest(parameters);
		setAuthenticationCredentials(parameters, request);

		request.addBankAccount(bankAccount);

		return request;
	}
	
	/**
	 * Builds a subscription request
	 *
	 * @param parameters
	 *            The parameters to be sent to the server
	 * @return The complete subscription request
	 * @throws InvalidParametersException
	 */
	public static Request buildSubscriptionRequest(
			Map<String, String> parameters) throws InvalidParametersException {

		// Subscription basic parameters
		Integer trialDays = getIntegerParameter(parameters,
				PayU.PARAMETERS.TRIAL_DAYS);
		Boolean immediatePayment = getBooleanParameter(parameters,
				PayU.PARAMETERS.IMMEDIATE_PAYMENT);
		Integer quantity = getIntegerParameter(parameters,
				PayU.PARAMETERS.QUANTITY);
		Integer installments = getIntegerParameter(parameters,
				PayU.PARAMETERS.INSTALLMENTS_NUMBER);

		String subscriptionId = getParameter(parameters,
				PayU.PARAMETERS.SUBSCRIPTION_ID);
		Boolean termsAndConditionsAcepted=getBooleanParameter(parameters,
				PayU.PARAMETERS.TERMS_AND_CONDITIONS_ACEPTED);
		
		List<RecurringBillItem> recurringBillItems = buildRecurringBillItemList(parameters);

		// Plan parameter
		SubscriptionPlan plan = buildSubscriptionPlanRequest(parameters);
		String planId = getParameter(parameters, PayU.PARAMETERS.PLAN_ID);
		plan.setId(planId);
		if(planId!=null){
			plan.setAccountId(null);
		}

		// Customer parameter
		Customer customer = buildCustomerRequest(parameters);
		// CreditCard parameter
		if (CollectionsUtil.interceptMaps(parameters.keySet(), CREDIT_CARD_VALID_PARAMS)) {
			PaymentPlanCreditCard cc = buildCreditCardRequest(parameters);
			if (parameters.containsKey(PayU.PARAMETERS.TOKEN_ID)) {
				cc.setAddress(null);
			}
			cc.setCustomerId(null);
			customer.addCreditCard(cc);
		}
		// BankAccount parameter
		if (CollectionsUtil.interceptMaps(parameters.keySet(), BANK_ACCOUNT_VALID_PARAMS)) {
			BankAccount bankAccount=buildBankAccountRequest(parameters);
			bankAccount.setCustomerId(null);
			if(bankAccount.getId()!=null){
				bankAccount.setAccountId(null);
			}
			customer.addBankAccount(bankAccount);
		}
		
		// Delivery address parameters
		Address deliveryAddress = new Address();
		deliveryAddress.setLine1(getParameter(parameters, PayU.PARAMETERS.DELIVERY_ADDRESS_1));
		deliveryAddress.setLine2(getParameter(parameters, PayU.PARAMETERS.DELIVERY_ADDRESS_2));
		deliveryAddress.setLine3(getParameter(parameters, PayU.PARAMETERS.DELIVERY_ADDRESS_3));
		deliveryAddress.setCity(getParameter(parameters, PayU.PARAMETERS.DELIVERY_CITY));
		deliveryAddress.setState(getParameter(parameters, PayU.PARAMETERS.DELIVERY_STATE));
		deliveryAddress.setCountry(getParameter(parameters, PayU.PARAMETERS.DELIVERY_COUNTRY));
		deliveryAddress.setPostalCode(getParameter(parameters, PayU.PARAMETERS.DELIVERY_POSTAL_CODE));
		deliveryAddress.setPhone(getParameter(parameters, PayU.PARAMETERS.DELIVERY_PHONE));
		
		// Subscription notifyUrl, sourceReference, extra1 and extra 2 parameters
		String notifyUrl = getParameter(parameters, PayU.PARAMETERS.NOTIFY_URL);
		String sourceReference = getParameter(parameters, PayU.PARAMETERS.SOURCE_REFERENCE);
		String extra1 = getParameter(parameters, PayU.PARAMETERS.EXTRA1);
		String extra2 = getParameter(parameters, PayU.PARAMETERS.EXTRA2);
		
		// Subscription sourceId and description
		Long sourceId = getLongParameter(parameters, PayU.PARAMETERS.SOURCE_ID);
		String description = getParameter(parameters, PayU.PARAMETERS.DESCRIPTION);
		SubscriptionCreationSource creationSource = getEnumValueParameter(SubscriptionCreationSource.class, parameters,
				PayU.PARAMETERS.CREATION_SOURCE);
		
		// Subscription basic parameters
		Subscription request = new Subscription();
		setAuthenticationCredentials(parameters, request);

		request.setTrialDays(trialDays);
		request.setImmediatePayment(immediatePayment);
		request.setQuantity(quantity);
		request.setInstallments(installments);
		request.setTermsAndConditionsAcepted(termsAndConditionsAcepted);
		request.setDeliveryAddress(deliveryAddress);
		request.setRecurringBillItems(recurringBillItems);
		request.setNotifyUrl(notifyUrl);
		request.setSourceReference(sourceReference);
		request.setExtra1(extra1);
		request.setExtra2(extra2);
		request.setSourceId(sourceId);
		request.setDescription(description);
		
		if (creationSource != null) {
			request.setCreationSource(creationSource.name());
		}

		// Subscription complex parameters (customer and plan)
		request.setPlan(plan);
		request.setCustomer(customer);
		request.setId(subscriptionId);

		return request;
	}
	
	/**
	 * Builds a recurring billing item request without authentication
	 * 
	 * @param parameters
	 *            The parameters to be sent to the server
	 * @return List<RecurringBillItem> The RecurringBillItems list
	 * @throws InvalidParametersException
	 */
	private static List<RecurringBillItem> buildRecurringBillItemList(Map<String, String> parameters)
			throws InvalidParametersException {
		
		if (parameters.containsKey(PayU.PARAMETERS.SUBSCRIPTION_EXTRA_CHARGES_DESCRIPTION)) {
			String description = getParameter(parameters, PayU.PARAMETERS.SUBSCRIPTION_EXTRA_CHARGES_DESCRIPTION);
			BigDecimal value = getBigDecimalParameter(parameters, PayU.PARAMETERS.ITEM_VALUE);
			BigDecimal taxValue = getBigDecimalParameter(parameters, PayU.PARAMETERS.ITEM_TAX);
			BigDecimal taxReturnBase = getBigDecimalParameter(parameters, PayU.PARAMETERS.ITEM_TAX_RETURN_BASE);
			Currency currency = getEnumValueParameter(Currency.class, parameters, PayU.PARAMETERS.CURRENCY);
			RecurringBillItem recurringBillItem = new RecurringBillItem();
			recurringBillItem.setDescription(description);
			List<AdditionalValue> additionalValues = buildItemAdditionalValues(currency, value, taxValue, taxReturnBase);
			recurringBillItem.setAdditionalValues(additionalValues);
			return Arrays.asList(recurringBillItem);
		}
		
		return Collections.emptyList();
	}
	
	/**
	 * Builds an update subscription request
	 *
	 * @param parameters
	 *            The parameters to be sent to the server
	 * @return The complete subscription request
	 * @throws InvalidParametersException
	 */
	public static Request buildSubscriptionUpdateRequest(
			Map<String, String> parameters) throws InvalidParametersException {

		String newCreditCardToken = getParameter(parameters,
				 PayU.PARAMETERS.TOKEN_ID);

		String newBankAccountId = getParameter(parameters,
				 PayU.PARAMETERS.BANK_ACCOUNT_ID);

		String subscriptionId = getParameter(parameters,
				PayU.PARAMETERS.SUBSCRIPTION_ID);
		
		Subscription request = new Subscription();
		
		// Set the delivery address fields
		Address deliveryAddress = new Address();
		deliveryAddress.setLine1(getParameter(parameters,PayU.PARAMETERS.DELIVERY_ADDRESS_1));
		deliveryAddress.setLine2(getParameter(parameters,PayU.PARAMETERS.DELIVERY_ADDRESS_2));
		deliveryAddress.setLine3(getParameter(parameters,PayU.PARAMETERS.DELIVERY_ADDRESS_3));
		deliveryAddress.setCity(getParameter(parameters,PayU.PARAMETERS.DELIVERY_CITY));
		deliveryAddress.setState(getParameter(parameters,PayU.PARAMETERS.DELIVERY_STATE));
		deliveryAddress.setCountry(getParameter(parameters,PayU.PARAMETERS.DELIVERY_COUNTRY));
		deliveryAddress.setPostalCode(getParameter(parameters,PayU.PARAMETERS.DELIVERY_POSTAL_CODE));
		deliveryAddress.setPhone(getParameter(parameters,PayU.PARAMETERS.DELIVERY_PHONE));
		
		setAuthenticationCredentials(parameters, request);

		request.setUrlId(subscriptionId);
		request.setCreditCardToken(newCreditCardToken);
		request.setBankAccountId(newBankAccountId);
		request.setDeliveryAddress(deliveryAddress);
		
		return request;

	}

	/**
	 * Builds a recurring bill item request
	 *
	 * @param parameters
	 *            The parameters to be sent to the server
	 * @return The complete recurring bill item request
	 * @throws InvalidParametersException
	 */
	public static Request buildRecurringBillItemRequest(
			Map<String, String> parameters) throws InvalidParametersException {

		// Recurring bill item basic parameters
		BigDecimal value = getBigDecimalParameter(parameters,
				PayU.PARAMETERS.ITEM_VALUE);

		Currency currency = getEnumValueParameter(Currency.class, parameters,
				PayU.PARAMETERS.CURRENCY);

		String description = getParameter(parameters,
				PayU.PARAMETERS.DESCRIPTION);

		BigDecimal taxValue = getBigDecimalParameter(parameters,
				PayU.PARAMETERS.ITEM_TAX);

		BigDecimal taxReturnBase = getBigDecimalParameter(parameters,
				PayU.PARAMETERS.ITEM_TAX_RETURN_BASE);

		String recurringBillItemId = getParameter(parameters,
				PayU.PARAMETERS.RECURRING_BILL_ITEM_ID);

		String recurringItemId = getParameter(parameters,
				PayU.PARAMETERS.RECURRING_ITEM_ID);

		String subscriptionId = getParameter(parameters,
				PayU.PARAMETERS.SUBSCRIPTION_ID);

		// Subscription basic parameters
		RecurringBillItem request = new RecurringBillItem();
		setAuthenticationCredentials(parameters, request);

		request.setId(recurringBillItemId);
		request.setDescription(description);
		request.setSubscriptionId(subscriptionId);
		request.setRecurringBillId(recurringItemId);
		List<AdditionalValue> additionalValues = buildItemAdditionalValues(
				currency, value, taxValue, taxReturnBase);
		request.setAdditionalValues(additionalValues);

		return request;
	}

	/**
	 * Builds a recurring bill item request
	 *
	 * @param parameters
	 *            The parameters to be sent to the server
	 * @return The complete recurring bill item request
	 * @throws InvalidParametersException
	 * @throws PayUException
	 */
	public static Request buildRecurringBillItemListRequest(
			Map<String, String> parameters) throws InvalidParametersException, PayUException {

		String description = getParameter(parameters,
				PayU.PARAMETERS.DESCRIPTION);

		String subscriptionId = getParameter(parameters,
				PayU.PARAMETERS.SUBSCRIPTION_ID);
		Map<String, String> parametersRequest=new HashMap<String, String>();
		parametersRequest.put(PayU.PARAMETERS.SUBSCRIPTION_ID, subscriptionId);
		parametersRequest.put(PayU.PARAMETERS.DESCRIPTION, description);
		RecurringBillItemListRequest request = new RecurringBillItemListRequest();
		setAuthenticationCredentials(parameters, request);
		request.setMap(parametersRequest);

		return request;
	}

	/**
	 * Builds a recurring bill request
	 *
	 * @param parameters The parameters to be sent to the server
	 * @return The complete recurring bill request
	 * @throws InvalidParametersException
	 */
	public static Request buildRecurringBillRequest(
			Map<String, String> parameters) throws InvalidParametersException {

		// Recurring bill basic parameters
		String recurringBillId = getParameter(parameters, PayU.PARAMETERS.RECURRING_BILL_ID);

		RecurringBill request = new RecurringBill();
		setAuthenticationCredentials(parameters, request);
		request.setId(recurringBillId);

		return request;
	}

	/**
	 * Builds a recurring bill list request
	 *
	 * @param parameters The parameters to be sent to the server
	 * @return The complete recurring bill list request
	 * @throws InvalidParametersException
	 * @throws PayUException
	 */
	public static Request buildRecurringBillListRequest(
			Map<String, String> parameters) throws InvalidParametersException, PayUException {

		String startDate = getParameter(parameters, PayU.PARAMETERS.RECURRING_BILL_DATE_BEGIN);
		String endDate = getParameter(parameters, PayU.PARAMETERS.RECURRING_BILL_DATE_FINAL);

		// Validates the PaymentMethodType value, if any.
		getEnumValueParameter(
				PaymentMethodType.class, parameters, PayU.PARAMETERS.RECURRING_BILL_PAYMENT_METHOD_TYPE);

		validateDateParameter(
				startDate, PayU.PARAMETERS.RECURRING_BILL_DATE_BEGIN, Constants.DEFAULT_DATE_WITHOUT_HOUR_FORMAT);
		validateDateParameter(
				endDate, PayU.PARAMETERS.RECURRING_BILL_DATE_FINAL, Constants.DEFAULT_DATE_WITHOUT_HOUR_FORMAT);

		RecurringBillListRequest request = new RecurringBillListRequest();
		setAuthenticationCredentials(parameters, request);
		request.setMap(parameters);

		return request;
	}


	/**
	 * Builds a subscription request
	 *
	 * @param parameters
	 *            The parameters to be sent to the server
	 * @return The complete subscription request
	 * @throws InvalidParametersException
	 * @throws PayUException
	 */
	public static Request buildSubscriptionListRequest(
			Map<String, String> parameters) throws InvalidParametersException, PayUException {

		// Subscription parameters

		String customerId = getParameter(parameters,
				PayU.PARAMETERS.CUSTOMER_ID);
		String planCode = getParameter(parameters,
				PayU.PARAMETERS.PLAN_CODE);
		String planId = getParameter(parameters,
				PayU.PARAMETERS.PLAN_ID);
		String state = getParameter(parameters,
				PayU.PARAMETERS.STATE);
		String subscriptionId = getParameter(parameters,
				PayU.PARAMETERS.SUBSCRIPTION_ID);
		String accountId=getParameter(parameters,
				PayU.PARAMETERS.ACCOUNT_ID);
		String limit=getParameter(parameters,
				PayU.PARAMETERS.LIMIT);
		String offset=getParameter(parameters,
				PayU.PARAMETERS.OFFSET);
		String sourceId = getParameter(parameters, 
				PayU.PARAMETERS.SOURCE_ID);

		Map<String, String> paramsFilter=new HashMap<String, String>();
		paramsFilter.put(PayU.PARAMETERS.CUSTOMER_ID, customerId);
		paramsFilter.put(PayU.PARAMETERS.PLAN_CODE, planCode);
		paramsFilter.put(PayU.PARAMETERS.PLAN_ID, planId);
		paramsFilter.put(PayU.PARAMETERS.STATE, state);
		paramsFilter.put(PayU.PARAMETERS.SUBSCRIPTION_ID,subscriptionId);
		paramsFilter.put(PayU.PARAMETERS.ACCOUNT_ID, accountId);
		paramsFilter.put(PayU.PARAMETERS.LIMIT, limit);
		paramsFilter.put(PayU.PARAMETERS.OFFSET, offset);
		paramsFilter.put(PayU.PARAMETERS.SOURCE_ID, sourceId);

		SubscriptionsListRequest request = new SubscriptionsListRequest();

		request.setMap(paramsFilter);
		setAuthenticationCredentials(parameters, request);

		return request;
	}
}
