package sieve.property.model.dto

import java.math.*
import java.sql.*
import java.text.SimpleDateFormat;

class Address implements java.io.Serializable {
	String pattern = "MM/dd/yyyy";
	SimpleDateFormat format = new SimpleDateFormat(pattern);
	Timestamp zeroDate = new java.sql.Timestamp(0)
	Integer id = 0
	Integer version = 0
	String	fullAddress = ""
	String	streetName = ""
	int		mailingAddress = 0
	int		billingAddress = 0
	String	city = ""
	String	state = ""
	String	zipCode = ""
	String  zipPlusFour = ""
	Integer	countryId 		//(FK to country.id)
	String	province = ""
	String	county = ""
	BigDecimal	latitude = BigDecimal.ZERO
	BigDecimal	longitude = BigDecimal.ZERO
	Timestamp createDate
	Timestamp updateDate
	
	public boolean isEquivalent(Address myAddress) {
	    if (myAddress.fullAddress.equalsIgnoreCase(this.fullAddress)) {
			return true
		} else {
		    return false
		}
	}
	
	public String toString() {
		return """AddressBean:
    id 					--	${id}
	fullAddress 		--	${fullAddress}
	streetName 			--	${streetName}
	mailingAddress 		--	${mailingAddress}
	billingAddress 		--	${billingAddress}
	city 				--	${city}
	state 				--	${state}
	zipCode 			--	${zipCode}
	countryId 			--	${countryId}
	province 			--	${province}
	county 				--	${county}
	latitude 			--	${latitude}
	longitude 			--	${longitude}
    createDate          --  ${createDate}
    updateDate          --  ${updateDate}
      """
	}
	
}
