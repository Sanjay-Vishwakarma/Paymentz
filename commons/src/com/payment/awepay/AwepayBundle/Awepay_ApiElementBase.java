package com.payment.awepay.AwepayBundle;

import java.util.*;
import java.lang.reflect.*;

abstract public class Awepay_ApiElementBase {

	ArrayList<String> _errors;

	public Awepay_ApiElementBase() {
		this._errors = new ArrayList<String>();
	}

	protected void addError(String error) {
		this._errors.add(error);
	}

	protected void addErrors(String prefix, List<String> errors) {
		for (int i = 0; i < errors.size(); i++) {
			this.addError("(".concat(prefix).concat(") ").concat(errors.get(i)));
		}
	}

	public ArrayList<String> getErrors() {
		return this._errors;
	}

	protected String padLeft(String str, int length, String padChar) {
		String strReturn = str.toString();
		if (str.length() < length) {
			for (int i = str.length(); i < length; i++) {
				strReturn = padChar.concat(strReturn);
			}
		}
		return strReturn;
	}

	protected boolean validate() {
		return true;
	}

	protected Map<String, Object> toArray() {
		Map<String, Object> arrayData = new HashMap<String, Object>();
		Field[] fields = this.getClass().getFields();

		for (int i = 0; i < fields.length; i++) {
			if (fields[i].getName().startsWith("_") || fields[i].getName().matches("[A-Z].*")) {
				continue;
			}
			try {
				Field field = this.getClass().getField(fields[i].getName());
				if (Awepay_ApiElementBase.class.isAssignableFrom(field.getType())) {
					arrayData.put(field.getName(), ((Awepay_ApiElementBase) field.get(this)).toArray());
				} else if (List.class.isAssignableFrom(field.getType())) {
					List arrayValue = (List) field.get(this);
					List<Object> subArrayData = new ArrayList<Object>();
					for (int j = 0; j < arrayValue.size(); j++) {
						if (Awepay_ApiElementBase.class.isAssignableFrom(arrayValue.get(j).getClass())) {
							subArrayData.add(((Awepay_ApiElementBase) arrayValue.get(j)).toArray());
						} else {
							subArrayData.add(arrayValue.get(j));
						}
					}
					arrayData.put(field.getName(), subArrayData);
				} else {
					if (field.getName() == "className") {
						arrayData.put("class", field.get(this));
					} else {
						arrayData.put(field.getName(), field.get(this));
					}
				}
			} catch (Exception e) {
				continue;
			}
		}

		return arrayData;
	}

}
