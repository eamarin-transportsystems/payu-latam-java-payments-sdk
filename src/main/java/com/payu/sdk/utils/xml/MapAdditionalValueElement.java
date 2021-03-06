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
package com.payu.sdk.utils.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.payu.sdk.model.AdditionalValue;

/**
 * Utility to encapsulate multiple {@link MapAdditionalValueEntry} into elements.
 *
 * @author PayU Latam
 * @since 1.0.0
 * @version 1.0.0, 21/08/2013
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "additionalValue")
public class MapAdditionalValueElement {

	/**
	 * The list of additional values
	 */
	@XmlElement(name = "entry")
	private List<MapAdditionalValueEntry> entries = new ArrayList<MapAdditionalValueEntry>();

	/**
	 * Returns the list of entries
	 *
	 * @return the entries
	 */
	public List<MapAdditionalValueEntry> getEntries() {
		return entries;
	}

	/**
	 * Sets the list of entries
	 *
	 * @param entries The entries to be set
	 */
	public void setEntries(List<MapAdditionalValueEntry> entries) {
		this.entries = entries;
	}

	/**
	 * Adds an entry to the list
	 *
	 * @param key The key the entry will have
	 * @param additionalValue The additional value to be added
	 */
	public void addEntry(String key, AdditionalValue additionalValue) {
		MapAdditionalValueEntry entry = new MapAdditionalValueEntry();
		entry.setKey(key);
		entry.setAdditionalValue(additionalValue);
		entries.add(entry);
	}
}
