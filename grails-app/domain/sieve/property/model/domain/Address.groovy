package sieve.property.model.domain

class Address {
	static belongsTo = Property
	
    static constraints = {  
		fullAddress unique: true, nullable: false
		streetName nullable: true
		mailingAddress nullable: true
		billingAddress nullable: true
		city nullable: true
		state nullable: true
		zipCode nullable: true
		zipPlusFour nullable: true
		countryId nullable: true		//(FK to country.id)
		province nullable: true
		county nullable: true
		latitude nullable: true
		longitude nullable: true
    }
	
	String	fullAddress = ""
	String	streetName = ""
	int		mailingAddress = 0
	int		billingAddress = 0
	String	city = ""
	String	state = ""
	String	zipCode = ""
	String  zipPlusFour = ""
	Integer	countryId = 0		//(FK to country.id)
	String	province = ""
	String	county = ""
	BigDecimal	latitude = BigDecimal.ZERO
	BigDecimal	longitude = BigDecimal.ZERO
	Date	dateCreated
	Date	lastUpdated
	
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
	dateCreated 		--	${dateCreated}
	lastUpdated 		--	${lastUpdated}
      """
	}
}

