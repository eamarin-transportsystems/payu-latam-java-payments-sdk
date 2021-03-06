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
package com.payu.sdk;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.payu.sdk.constants.Resources.RequestMethod;
import com.payu.sdk.exceptions.ConnectionException;
import com.payu.sdk.exceptions.InvalidParametersException;
import com.payu.sdk.exceptions.PayUException;
import com.payu.sdk.exceptions.SDKException.ErrorCode;
import com.payu.sdk.helper.HttpClientHelper;
import com.payu.sdk.model.Bank;
import com.payu.sdk.model.PaymentCountry;
import com.payu.sdk.model.PaymentMethodComplete;
import com.payu.sdk.model.TransactionResponse;
import com.payu.sdk.model.TransactionType;
import com.payu.sdk.model.response.ResponseCode;
import com.payu.sdk.payments.model.BankListResponse;
import com.payu.sdk.payments.model.PaymentMethodListResponse;
import com.payu.sdk.payments.model.PaymentMethodResponse;
import com.payu.sdk.payments.model.PaymentResponse;
import com.payu.sdk.utils.CommonRequestUtil;
import com.payu.sdk.utils.PaymentMethodMap;
import com.payu.sdk.model.PaymentMethodApi;
import com.payu.sdk.utils.RequestUtil;

/**
 * Manages all PayU payments operations
 *
 * @author PayU Latam
 * @since 1.0.0
 * @version 1.0.0, 21/08/2013
 */
public final class PayUPayments extends PayU {

	/**
	 * Private constructor
	 */
	private PayUPayments() {
	}

	/**
	 * Makes a ping petition
	 *
	 * @return true if the ping is done successfully
	 * @throws PayUException
	 * @throws ConnectionException
	 */
	public static boolean doPing() throws PayUException, ConnectionException {
		String res = HttpClientHelper.sendRequest(
				RequestUtil.buildPaymentsPingRequest(), RequestMethod.POST);

		PaymentResponse response = PaymentResponse.fromXml(res);

		return ResponseCode.SUCCESS.equals(response.getCode());
	}

	/**
	 * Makes a get payment methods petition
	 *
	 * @return The payment method list
	 * @throws PayUException
	 * @throws ConnectionException
	 */
	public static List<PaymentMethodComplete> getPaymentMethods()
			throws PayUException, ConnectionException {
		String res = HttpClientHelper.sendRequest(
				RequestUtil.buildPaymentMethodsListRequest(),
				RequestMethod.POST);

		PaymentMethodListResponse response = PaymentMethodListResponse
				.fromXml(res);

		return response.getPaymentMethods();

	}

	/**
	 * Get payment method Availability
	 *
	 * @return The payment method list
	 * @throws PayUException
	 * @throws ConnectionException
	 */
	public static PaymentMethodApi getPaymentMethodAvailability(String paymentMethod)
			throws PayUException, ConnectionException {
		//find the PaymentMethod object in the map
		PaymentMethodApi paymentMethodApi = PaymentMethodMap.getInstance().getPaymentMethod(paymentMethod);
		if (paymentMethodApi == null){
			//Makes a get payment method Availability request in the API
			paymentMethodApi = getPaymentMethodAvailabilityFromAPI(paymentMethod);
			//if obtains the paymentMethod object, adds this to the map
			if (paymentMethodApi != null){
				PaymentMethodMap.getInstance().putPaymentMethod(paymentMethodApi);
			}
		}
		return paymentMethodApi;
	}

	/**
	 * Makes a get payment method Availability request
	 * @param paymentMethod
	 * @return
	 * @throws PayUException
	 * @throws ConnectionException
	 */
	protected static PaymentMethodApi getPaymentMethodAvailabilityFromAPI(String paymentMethod)
			throws PayUException, ConnectionException {
		String res = HttpClientHelper.sendRequest(
				RequestUtil.buildPaymentMethodAvailability(paymentMethod),
				RequestMethod.POST);

		PaymentMethodResponse response = PaymentMethodResponse.fromXml(res);

		return response.getPaymentMethod();
	}

	/**
	 * Makes a get PSE Banks petition
	 *
	 * @param parameters
	 *            The parameters to be sent to the server
	 * @return The bank list information
	 * @throws PayUException
	 * @throws InvalidParametersException
	 * @throws ConnectionException
	 */
	public static List<Bank> getPSEBanks(Map<String, String> parameters)
			throws PayUException, InvalidParametersException,
			ConnectionException {

		RequestUtil.validateParameters(parameters, PayU.PARAMETERS.COUNTRY);

		PaymentCountry paymentCountry = PaymentCountry.valueOf(parameters
				.get(PayU.PARAMETERS.COUNTRY));

		String res = HttpClientHelper.sendRequest(
				RequestUtil.buildBankListRequest(paymentCountry),
				RequestMethod.POST);

		BankListResponse response = BankListResponse.fromXml(res);

		return response.getBanks();

	}

	/**
	 * Do an authorization transaction
	 *
	 * @param parameters
	 *            The parameters to be sent to the server
	 * @return The transaction response to the request sent
	 * @throws PayUException
	 * @throws ConnectionException
	 * @throws InvalidParametersException
	 */
	public static TransactionResponse doAuthorization(
			Map<String, String> parameters) throws PayUException,
			InvalidParametersException, ConnectionException {

		return doPayment(parameters, TransactionType.AUTHORIZATION);
	}

	/**
	 * Do a capture transaction
	 *
	 * @param parameters
	 *            The parameters to be sent to the server
	 * @return The transaction response to the request sent
	 * @throws PayUException
	 * @throws ConnectionException
	 * @throws InvalidParametersException
	 */
	public static TransactionResponse doCapture(Map<String, String> parameters)
			throws PayUException, InvalidParametersException,
			ConnectionException {

		return processTransaction(parameters, TransactionType.CAPTURE);
	}

	/**
	 * Do an authorization and capture transaction
	 *
	 * @param parameters
	 *            The parameters to be sent to the server
	 * @return The transaction response to the request sent
	 * @throws PayUException
	 * @throws ConnectionException
	 * @throws InvalidParametersException
	 */
	public static TransactionResponse doAuthorizationAndCapture(
			Map<String, String> parameters) throws PayUException,
			InvalidParametersException, ConnectionException {

		return doPayment(parameters, TransactionType.AUTHORIZATION_AND_CAPTURE, HttpClientHelper.SOCKET_TIMEOUT);
	}


	/**
	 * Do an authorization and capture transaction
	 *
	 * @param parameters
	 *            The parameters to be sent to the server
	 * @param the socket time out.
	 * @return The transaction response to the request sent
	 * @throws PayUException
	 * @throws ConnectionException
	 * @throws InvalidParametersException
	 */
	public static TransactionResponse doAuthorizationAndCapture(
			Map<String, String> parameters, Integer socketTimeOut) throws PayUException,
			InvalidParametersException, ConnectionException {

		return doPayment(parameters, TransactionType.AUTHORIZATION_AND_CAPTURE, socketTimeOut);
	}


	/**
	 * Do a void (Cancel) transaction
	 *
	 * @param parameters
	 *            The parameters to be sent to the server
	 * @return The transaction response to the request sent
	 * @throws PayUException
	 * @throws ConnectionException
	 * @throws InvalidParametersException
	 */
	public static TransactionResponse doVoid(Map<String, String> parameters)
			throws PayUException, InvalidParametersException,
			ConnectionException {

		return processTransaction(parameters, TransactionType.VOID);
	}

	/**
	 * Do a refund transaction
	 *
	 * @param parameters
	 *            The parameters to be sent to the server
	 * @return The transaction response to the request sent
	 * @throws PayUException
	 * @throws ConnectionException
	 * @throws InvalidParametersException
	 */
	public static TransactionResponse doRefund(Map<String, String> parameters)
			throws PayUException, InvalidParametersException,
			ConnectionException {

		return processTransaction(parameters, TransactionType.REFUND);
	}

	/**
	 * Makes payment petition
	 *
	 * @param parameters
	 *            The parameters to be sent to the server
	 * @param transactionType
	 *            The type of the payment transaction
	 * @return The transaction response to the request sent
	 * @throws PayUException
	 * @throws InvalidParametersException
	 * @throws ConnectionException
	 */
	private static TransactionResponse doPayment(
			Map<String, String> parameters, TransactionType transactionType)
			throws PayUException, InvalidParametersException,
			ConnectionException {

		return doPayment(parameters, transactionType, HttpClientHelper.SOCKET_TIMEOUT);
	}


	/**
	 * Makes payment petition
	 *
	 * @param parameters
	 *            The parameters to be sent to the server
	 * @param transactionType
	 *            The type of the payment transaction
	 * @param the socket time out.
	 * @return The transaction response to the request sent
	 * @throws PayUException
	 * @throws InvalidParametersException
	 * @throws ConnectionException
	 */
	private static TransactionResponse doPayment(
			Map<String, String> parameters, TransactionType transactionType, Integer socketTimeOut)
			throws PayUException, InvalidParametersException,
			ConnectionException {

		String[] required = getRequiredParameters(parameters);

		RequestUtil.validateParameters(parameters, required);

		String res = HttpClientHelper.sendRequest(
				RequestUtil.buildPaymentRequest(parameters, transactionType),
				RequestMethod.POST, socketTimeOut);

		PaymentResponse response = PaymentResponse.fromXml(res);

		return response.getTransactionResponse();
	}



	/**
	 * Gets the PaymentMethod value parameter from the parameters map.
	 * If the parameter is not null find the Payment method in the map, but if not found then make request to API
	 * @param parameters The parameters to be sent to the server
	 * @param paramName  the parameter to get
	 * @return the PaymentMethod value or null
	 */
	private static PaymentMethodApi getPaymentMethodParameter(Map<String, String> parameters, String paramName)
			throws PayUException, InvalidParametersException, ConnectionException{
		PaymentMethodApi paymentMethod = null;
		String parameter = CommonRequestUtil.getParameter(parameters, paramName);
		if (parameter != null){
			//find the PaymentMethod object in the map
			paymentMethod = getPaymentMethodAvailability(parameter);
		}
		return paymentMethod;
	}

	/**
	 *
	 * Returns the required parameters based on the payment method
	 *
	 * @param parameters
	 * @return
	 * @throws PayUException
	 * @throws InvalidParametersException
	 */
	private static String[] getRequiredParameters(Map<String, String> parameters)
			throws PayUException, InvalidParametersException, ConnectionException {

		//Initialize the list with default parameters
		List<String> requiredParameters= new ArrayList<String>();
		requiredParameters.add(PayU.PARAMETERS.REFERENCE_CODE);
		requiredParameters.add(PayU.PARAMETERS.DESCRIPTION);
		requiredParameters.add(PayU.PARAMETERS.CURRENCY);
		requiredParameters.add(PayU.PARAMETERS.VALUE);

		/* Token methods */
		if (parameters.containsKey(PayU.PARAMETERS.TOKEN_ID)) {
			requiredParameters.add(PayU.PARAMETERS.INSTALLMENTS_NUMBER);
			requiredParameters.add(PayU.PARAMETERS.TOKEN_ID);
			return requiredParameters.toArray(new String[requiredParameters.size()]);
		}

		RequestUtil.validateParameters(parameters, PayU.PARAMETERS.PAYMENT_METHOD);

		//Obtains the payment method. If the parameter is a value that doesn't available this return null and continue
		PaymentMethodApi paymentMethod = getPaymentMethodParameter(parameters,PayU.PARAMETERS.PAYMENT_METHOD);

		if (paymentMethod != null){
			if ("BOLETO_BANCARIO".equals(paymentMethod.getName())) {
				requiredParameters.add(PayU.PARAMETERS.PAYER_NAME);
				requiredParameters.add(PayU.PARAMETERS.PAYER_DNI);
				requiredParameters.add(PayU.PARAMETERS.PAYMENT_METHOD);
				requiredParameters.add(PayU.PARAMETERS.PAYER_STREET);
				requiredParameters.add(PayU.PARAMETERS.PAYER_STREET_2);
				requiredParameters.add(PayU.PARAMETERS.PAYER_CITY);
				requiredParameters.add(PayU.PARAMETERS.PAYER_STATE);
				requiredParameters.add(PayU.PARAMETERS.PAYER_POSTAL_CODE);

			}
			else if (paymentMethod.getType() != null){
				switch (paymentMethod.getType()) {
				case CASH:
					requiredParameters.add(PayU.PARAMETERS.PAYER_NAME);
					requiredParameters.add(PayU.PARAMETERS.PAYMENT_METHOD);
					break;
				case REFERENCED:
					requiredParameters.add(PayU.PARAMETERS.PAYER_NAME);
					requiredParameters.add(PayU.PARAMETERS.PAYMENT_METHOD);
					requiredParameters.add(PayU.PARAMETERS.PAYER_DNI);
					break;
				case PSE:
					requiredParameters.add(PayU.PARAMETERS.PAYER_NAME);
					requiredParameters.add(PayU.PARAMETERS.PAYMENT_METHOD);
					requiredParameters.add(PayU.PARAMETERS.PAYER_DOCUMENT_TYPE);
					requiredParameters.add(PayU.PARAMETERS.PAYER_DNI);
					requiredParameters.add(PayU.PARAMETERS.PAYER_EMAIL);
					requiredParameters.add(PayU.PARAMETERS.PAYER_CONTACT_PHONE);
					requiredParameters.add(PayU.PARAMETERS.PSE_FINANCIAL_INSTITUTION_CODE);
					requiredParameters.add(PayU.PARAMETERS.PAYER_PERSON_TYPE);
					requiredParameters.add(PayU.PARAMETERS.IP_ADDRESS);
					requiredParameters.add(PayU.PARAMETERS.COOKIE);
					requiredParameters.add(PayU.PARAMETERS.USER_AGENT);
					break;
				case CREDIT_CARD:

					boolean optionalSecurityCode = (parameters.containsKey(PayU.PARAMETERS.PROCESS_WITHOUT_CVV2) &&
							Boolean.TRUE.toString().equalsIgnoreCase(parameters.get(PayU.PARAMETERS.PROCESS_WITHOUT_CVV2)));

					requiredParameters.add(PayU.PARAMETERS.PAYER_NAME);
					requiredParameters.add(PayU.PARAMETERS.PAYMENT_METHOD);
					requiredParameters.add(PayU.PARAMETERS.INSTALLMENTS_NUMBER);
					requiredParameters.add(PayU.PARAMETERS.CREDIT_CARD_NUMBER);
					requiredParameters.add(PayU.PARAMETERS.CREDIT_CARD_EXPIRATION_DATE);
					if (!optionalSecurityCode) {
						requiredParameters.add(PayU.PARAMETERS.CREDIT_CARD_SECURITY_CODE);
					}
					break;
				default:
					throw new PayUException(ErrorCode.API_ERROR,"Unsupported payment method");
				}
			}
		}else{
			throw new PayUException(ErrorCode.API_ERROR, "Unsupported payment method");
		}
		return requiredParameters.toArray(new String[requiredParameters.size()]);
	}

	/**
	 * Makes a transaction dependent petition
	 *
	 * @param parameters
	 *            The parameters to be sent to the server
	 * @param transactionType
	 *            The type of the payment transaction
	 * @return The transaction response to the request sent
	 * @throws PayUException
	 * @throws InvalidParametersException
	 * @throws ConnectionException
	 */
	private static TransactionResponse processTransaction(
			Map<String, String> parameters, TransactionType transactionType)
			throws PayUException, InvalidParametersException,
			ConnectionException {

		String[] required = new String[] { PayU.PARAMETERS.ORDER_ID,
				PayU.PARAMETERS.TRANSACTION_ID };

		RequestUtil.validateParameters(parameters, required);

		String res = HttpClientHelper.sendRequest(
				RequestUtil.buildPaymentRequest(parameters, transactionType),
				RequestMethod.POST);

		PaymentResponse response = PaymentResponse.fromXml(res);

		return response.getTransactionResponse();
	}

}
