package org.mariner.data.scripts

import org.mariner.data.scripts.model.*
import com.gargoylesoftware.htmlunit.*
import com.gargoylesoftware.htmlunit.html.*
import com.gargoylesoftware.htmlunit.util.*

import java.awt.event.ItemEvent
import java.sql.ResultSet
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.nio.file.*

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.sql.Date
import java.util.Random
import java.sql.*

import groovy.sql.Sql
import groovy.sql.GroovyRowResult

/* NOTES: */
/* To see how to use XMLSlurper, read the docs for GPath */
/* getThumbs() uses system dependent file system paths... ie, C:\\temp\pics.  Should change this to a URI to make it more portable. */
//def sql = Sql.newInstance("jdbc:mysql://192.168.3.101:3306/pipeline", "pipeline", "pipeline", "com.mysql.jdbc.Driver")
// Debug specific vars
totalIOExceptions = 0
totalSQLExceptions = 0
totalNonSQLExceptions = 0
totalDuplicateAddresses = 0
totalDisconnectedAddresses = 0
property_desc_length = 2048		//This needs to map to the size of the property.description field in property table.
now = new java.sql.Timestamp(new java.util.Date().getTime())

def property 
GroovyRowResult savedProperty
def propHistory 
def address 
List <Image> images
//org.mariner.data.scripts.model.Image image 

String applicationName = "Netscape"
String applicationVersion = "5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.114 Safari/537.36"
String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.114 Safari/537.36"
int browserVersionNumeric = 17
BrowserVersionFeatures[] features = [BrowserVersionFeatures.CAN_INHERIT_CSS_PROPERTY_VALUES]
def dbConnection

def getBrowserVersion (String applicationName, String applicationVersion, String userAgent, int browserVersionNumeric,  BrowserVersionFeatures[] features) {
	return new BrowserVersion(applicationName, applicationVersion, userAgent, browserVersionNumeric, features)
}

//WebClient webClient = new WebClient(BrowserVersion.CHROME)
BrowserVersion browser = getBrowserVersion(applicationName, applicationVersion, userAgent, browserVersionNumeric, features)
WebClient webClient = new WebClient(browser)
webClient.getOptions().setJavaScriptEnabled(false);

// Suppress CSS warnings.
webClient.getOptions().setCssEnabled(false)
webClient.setIncorrectnessListener(new IncorrectnessListener() {
	@Override

	public void notify(String arg0, Object arg1) {
			// TODO Auto-generated method stub
	}
})

/*
 def getConnection() {
	def dbUser     = "nezfrwzgstrbrq"
	def dbPassword = "y4uT-C-py8LtZ5fu5fGf6HJgz2"
	def dbUrl      = "jdbc:postgresql://ec2-54-204-45-196.compute-1.amazonaws.com:5432/d2kbuqsn99jlgm?user=${dbUser}&password=${dbPassword}&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory"
	def dbDriver   = "org.postgresql.Driver"
	 
	def sql = Sql.newInstance(dbUrl, dbDriver)
}
 */

def getConnection() {
	/*
	def dbUser     = "scrapers"
	def dbPassword = "scrapers"
	def dbUrl      = "jdbc:mysql://192.168.1.9:42263/realestate&ssl=false"
	def dbDriver   = "com.mysql.jdbc.Driver"
	Class.forName ("com.mysql.jdbc.Driver").newInstance ();
	Connection conn = DriverManager.getConnection (url, "username", "password");
	*/
	
	def db = [url:'jdbc:mysql://192.168.1.9:42263/realestate', user:'scrapers', password:'scrapers', driver:'com.mysql.jdbc.Driver']
	def sql = Sql.newInstance(db.url, db.user, db.password, db.driver)
}

// Get the homepage
HtmlPage page = webClient.getPage('http://www.clarkhawaii.com/')
WebAssert.assertTitleContains(page, "Big Island Real Estate")

// Set search filters
// Set district to Puna	search_district_id #1
HtmlSelect region = page.getElementByName("loc_region")
//region.setSelectedAttribute("north kona", true)
//region.setSelectedAttribute("north hilo", true)
region.setSelectedAttribute("Any", true)
println("region HtmlSelect value is: ")
println(region);

// Set property type to 'Any'... choices are 158=Homes;160=Land;159=Condo;""=Any
HtmlSelect propertyType = page.getElementByName("Property_Type")
//propertyType.setSelectedAttribute("159", true)
propertyType.setSelectedAttribute("Any", true)
println("propertyType HtmlSelect value is: ")
println(propertyType);

// Set min price to 'Any', as in 0...
HtmlSelect priceMin = page.getElementByName("Min_Price")
//priceMin.setSelectedAttribute("100000", true)
priceMin.setSelectedAttribute("No Minumum", true)

// Set max price to something affordable...
HtmlSelect priceMax = page.getElementByName("Max_Price")
priceMax.setSelectedAttribute("250000", true)

// Get Results ...
List <HtmlForm> forms = (List) page.getByXPath("//form[@action='/search/url_search/' and @class='quick-search-component']")
//form action="/search/url_search/" class="quick-search-component"
HtmlForm form = forms.getAt(0)

//List <HtmlInput> searchSubmits = form.getByXPath("//input[@class='submit' and @type='submit' and @value='Search']")
List <HtmlInput> searchSubmits = form.getByXPath("//input[@type='submit' and @value='Search']")
HtmlSubmitInput searchButton = searchSubmits.getAt(0)
HtmlPage startPage = searchButton.click()
mainStreet(startPage)
println("Total IOExceptions = ${totalIOExceptions}")
println("Total SQLExceptions = ${totalSQLExceptions}")
webClient.closeAllWindows()

def mainStreet(startPage){
	def nextPage
	//try {
		dbConnection = getConnection()
		//initSequence()  // Need to do this to put the postgresql sequence in the correct state
		def links = startPage.getByXPath("//*[starts-with(@class, 'standard-property span')]/a")
		
		links.each { link ->
			processPageDetails(link, dbConnection)
//System.exit(0)
		}
		
		nextPage = startPage
		while (hasNextPage(nextPage)){
			nextPage = getNextPage(nextPage)
			links = nextPage.getByXPath("//*[starts-with(@class, 'standard-property span')]/a")
			links.each { link ->
				processPageDetails(link, dbConnection)
//System.exit(0)
			}
		}
		dbConnection.close()
	//} catch (Exception ex) {
		//showException("mainStreet", ex, "n", "no")
		//return "N/A"
	//}
}

def boolean hasNextPage(HtmlPage myPage){
	def myParser = getParser(myPage)
	
	// title="Next Page"
	myParser.'**'.find{ it.@title == 'Next Page'}.each{ item ->
		if (item) {
			println("Found a next page...................................................................................................................")
			return true
		} else {
			return false
		}
	}	
}

def getNextPage(HtmlPage myPage){
	
	// title="Next Page"
	//[1,2,3].first()
	HtmlAnchor nextPageLink = myPage.getByXPath("//a[@class='pagingNext' and @title='Next Page']")[0]
		//println("The item is ${item}")
	HtmlPage nextPage = nextPageLink.click()
    println("..........................................Got another page........................................................................................")
		
	return nextPage
}

def processPageDetails(link, dbConnection){
	def addressId
	def savedProperty

	property = new Property()
	propHistory = new PropertyHistory()
	address = new Address()
	images = new ArrayList<Image>()
	HtmlPage detailsPage = link.click()

	def type = property.propertyType = getPropertyType(detailsPage)
	property = getScrapedProperties(type, property, detailsPage)
	address = getAddress(address, detailsPage)
	images = getPics(detailsPage, images)

	try {
		if (addressExists(address)) {
			saveAddress(address, 'update')
			totalDuplicateAddresses++
		} else {
			saveAddress(address, 'insert')
		}

		addressId = getAddressId(address)
		address.id = addressId
		property.addressId = addressId

		// If there's already a property for this address then check for diffs...
		savedProperty = getPropertyAtAddress(address)

		// Check if the persisted property associated with this address significantly differs from the property currently associated with this address.
		// If so, copy the current Property to PropertyHistory and save the scraped property to Property table.  Otherwise, just update the current Property.
		if (savedProperty) {
			// If price or beds or baths or yearBuilt changes then move to history and use new property since a change in any of these properties means
			// it's significantly different.  If the new property is decidedly less informative, then instead of making the current property
			// also less informative (via an update), move the current property to history and insert the new less informative property in hopes
			// that this is just a temporary lack of data.  In other words, don't let a bad scrape corrupt and lose the property data.
			property.setEmptiness(savedProperty)
			if ((savedProperty.price != property.price) || ((savedProperty.beds != property.beds) || (savedProperty.baths != property.baths) ||
			savedProperty.year_built != property.yearBuilt) || ((property.numEmpty - property.threshold) > property.numOtherEmpty)){
				copyToPropertyHistory(savedProperty)
				deleteProperty(savedProperty)
				saveProperty(property, 'insert')
				/*  TODO: Send an email to webmaster regarding the disparity between properties
				 if ((property.numEmpty - property.threshold) > property.numOtherEmpty){
				 notify(property, savedProperty, "email")
				 }
				 */
			} else {
				saveProperty(property, 'update')
			}
		} else { // There was a bad situation where an address didn't exist but a property did.
			saveProperty(property, 'insert')
		}
		def propertyId = getPropertyId(address)
		property.id = propertyId
		saveImages(property.id)
		doCommit(dbConnection)
	} catch (SQLException sqlex) {
		println("Process page details:  Got fatal SQLException: " + sqlex.getMessage() + ", rolling back then exiting....")
		totalSQLExceptions++
		rollback()
	} catch (Exception ex) {
		println("Process page details:  Got fatal exception: " + ex.getMessage() + ", continuing....")
	}
}

def doCommit(dbConnection){
	// TODO: For testing db is on autocommit.  Find out if you need to turn autocommit off for GORM to work.
	try {
		dbConnection.commit()
	} catch (SQLException sqlex) {
		if (sqlex.getLocalizedMessage() =~ (/autocommit/)) {
			println("doCommit: Autocommit is on")
		} else {
		    println("doCommit: Autocommit is off")
		}
	}
}

def rollback() throws SQLException {
	try {
	    deleteImages()
	    deleteProperty()
	    deleteAddress()
	} catch (SQLException sqlex) {
	    println("rollback():  Failed to rollback transaction for objects:")
		println("             images:   " + images.toString() + "\n")
		println("             property: " + property.toString() + "\n")
		println("             address:  " + address.toString() + "\n")
		println("\n\n")
		throw sqlex
	}
}

def deleteImages() throws SQLException {
	def stmt = """delete from image
                  where property_id = ${property.id}
                    and date_created = ${now}
               """
	try {
	    dbConnection.execute(stmt)
	} catch (SQLException sqlex) {
	    println("deleteImages: Got sql exception deleting images with property_id = ${property.id}")
		totalSQLExceptions++
		throw sqlex
	}
}

def deleteProperty() throws SQLException {
	def stmt = """delete from property
                  where id = ${property.id}
               """
	try {
	    dbConnection.execute(stmt)
	} catch (SQLException sqlex) {
	    println("deleteProperty: Got sql exception deleting property with property_id = ${property.id}")
		totalSQLExceptions++
		throw sqlex
	}
}

def deleteAddress() throws SQLException {
	def stmt = """delete from address
                  where id = ${address.id}
               """
	try {
	    dbConnection.execute(stmt)
	} catch (SQLException sqlex) {
	    println("deleteAddress: Got sql exception deleting address with address_id = ${address.id}")
		totalSQLExceptions++
		throw sqlex
	}
}

def saveImages(Integer propertyId) throws SQLException {
	int timeToSleep = 0
	def savedImages = [:]
	//def propertyId = getPropertyId(dbConnection, property)
	
	try {
	    dbConnection.eachRow( "select * from image where property_id = ${propertyId}" ) { 
		    savedImages[$it.size] = it
	    }
	
	    images.each { image ->
		    image.propertyId = propertyId
		    if (savedImages[image.size]) {
			    def existingImage = savedImages[image.size]
			    deleteImagesWithIdAndSize(propertyId, image.size)
			    doCommit(dbConnection)
			    image.dateCreated = existingImage.dateCreated
			    image.lastUpdated = now
		    }
		
		    saveImage(image, "insert")
	    }
    } catch (SQLException sqlex) {
		throw sqlex
    }
}

def deleteImagesWithIdAndSize(Integer propertyId, int size) throws SQLException {
	stmt = """delete from image
              where property_id = ${propertyId}
                and size = ${size}
	       """
    try {
        dbConnection.execute(stmt)
    } catch (SQLException pex) {
		println("Got exception deleting from Image table where property_id = ${propertyId}: " + sqlex.getMessage() + "\n\n")
        totalSQLExceptions++
		throw pex
    }
}

def saveImage(Image anImage, String action) throws SQLException {
	switch (action) {
		case ~/insert/:
			try {
				insertImage(anImage)
			} catch (SQLException sqlex) {
				//println("Got exception inserting into Image table: " + sqlex.getMessage() + "\n\n")
				throw sqlex
			}
			break
		case ~/update/:
			try {
				updateImage(anImage)
			} catch (SQLException sqlex) {
				//println("Got exception updating Image table: " + sqlex.getMessage() + "\n\n")
				throw sqlex
			}
			break
		default:
			println("saveImage(): Unknow action - |${action}|")
			break
	}
	
}

def insertImage(Image image) throws SQLException {
	def stmt = ""
	int nextval = 0

	try {
		//nextval = getNextId()
		stmt = """insert into image (version, content, date_created, height, property_id, type, update_user, width)
                	values (0, ${image.content}, ${now}, ${image.height}, ${image.propertyId}, 
                         ${image.type}, ${image.updateUser}, ${image.width}
						 )
	       	   """
		dbConnection.execute(stmt)
	} catch (SQLException sqlex) {
		println("Got exception inserting into Image table: " + sqlex.getMessage() + "\n\n" + "Data = " + "\n" + image.toString())
		totalSQLExceptions++
		throw sqlex
	}
}

def updateAddress(Address myAddress) throws SQLException {
	def stmt = ""
	
	stmt = """update address set full_address = ${myAddress.fullAddress}, street_name = ${myAddress.streetName}, mailing_address = ${myAddress.mailingAddress}, 
                                 billing_address = ${myAddress.billingAddress}, city = ${myAddress.city}, state = ${myAddress.state}, zip_code = ${myAddress.zipCode}, 
                                 zip_plus_four = ${myAddress.zipPlusFour}, country_id = ${myAddress.countryId}, province = ${myAddress.province}, 
                                 county = ${myAddress.county}, latitude = ${myAddress.latitude}, longitude = ${myAddress.longitude},
								 last_updated = ${now}
			  where id = ${myAddress.id}
           """
	try {
	    dbConnection.execute(stmt)
	} catch (SQLException sqlex) {
		println("Got exception updating Address table: " + sqlex.getMessage() + "\n\n" + "Data = " + "\n" + myAddress.toString())
		totalSQLExceptions++
		throw sqlex
	}
}

def updateImage(Image image) throws SQLException {
	def stmt = ""
	stmt = """ update image set content = ${image.content}, height = ${image.height}, 
                      property_id = ${image.propertyId}, type = ${image.type}, last_updated = ${now}, update_user = ${image.updateUser},
                      width = ${image.width}
               where id = ${image.id}
	       """
	try {
	    dbConnection.execute(stmt)
	} catch (SQLException sqlex) {
		println("Got exception updating Image table: " + sqlex.getMessage() + "\n\n" + "Data = " + "\n" + image.toString())
		totalSQLExceptions++
		throw sqlex
	}
}

def saveAddress(Address myAddress, String action) throws SQLException {
	
	switch (action) {
		case ~/insert/:
			try {
				insertAddress(myAddress)
			} catch (SQLException sqlex) {
				//println("Got exception inserting into Address table: " + sqlex.getMessage() + "\n\n")
				throw sqlex
			}
		break
		case ~/update/:
			try {
				updateAddress(myAddress)
			} catch (SQLException sqlex) {
				//println("Got exception updating Address table: " + sqlex.getMessage() + "\n\n")
				throw sqlex
			}
		break
		default:
			println("saveAddress(): Unknow action - |${action}|")
			return false
		break
	}
	return true
}
	
	
def insertAddress(Address address) throws SQLException {
	def stmt = ""
	int nextval = 0

	//nextval = getNextId()
	stmt = """insert into address (version, full_address, street_name, mailing_address, billing_address, 
                                   city, state, zip_code, zip_plus_four, country_id, province, county, latitude, longitude, date_created) 
              values (0, ${address.fullAddress}, ${address.streetName}, ${address.mailingAddress}, ${address.billingAddress}, ${address.city}, 
                      ${address.state}, ${address.zipCode}, ${address.zipPlusFour}, ${address.countryId}, ${address.province}, 
                      ${address.county}, ${address.latitude}, ${address.longitude}, ${this.now})
		   """
	try {
		dbConnection.execute(stmt)
	} catch (SQLException sqlex) {
		println("insertAddress() - Got SQL exception inserting into Address table: " + sqlex.getMessage() + "\n\n" + "Data = " + "\n" + address.toString())
		totalSQLExceptions++
		throw sqlex
	} catch (Exception ex) {
		println("insertAddress() - Got exception inserting into Address table: " + ex.getMessage() + "\n\n" + "Data = " + "\n" + address.toString())
		totalNonSQLExceptions++
		throw ex
	}
}

/*  NOTE: Must call this once per session (in our case a session is once per script run) or postgresql won't know about hibernate_sequence. */
def initSequence(){
	def stmt = "select nextval('hibernate_sequence') as nextId"
	def results = dbConnection.firstRow(stmt).nextId
	def nextId = results
}

def getNextId() throws SQLException {
	def stmt = "select currval('hibernate_sequence') as currentVal"
	try {
		def results = dbConnection.firstRow(stmt).currentVal
		def nextval = results + 1
		nextval
	} catch (SQLException sqlex) {
		println("getNextId(): Got SQL exception selecting nextval from hibernate_sequence: " + sqlex.getMessage() + "\n\n")
		totalSQLExceptions++
		throw sqlex
	}
}

def getAddressId(Address address) throws SQLException {
	def addressId
	
	stmt = """select id 
              from address 
              where full_address = ${address.fullAddress}
		   """
	try {
	    row = dbConnection.firstRow(stmt)
		if (row) {
			addressId = row.id
		} else {
			addressId = null
		}
	} catch (SQLException sqlex) {
		println("Got SQL exception selecting id from address with fullAddress of : " + address.fullAddress + "\n" + sqlex.getMessage() + "\n\n" )
		totalSQLExceptions++
	    throw sqlex
	}
	return addressId
}

// Assumes can only have one property per address
def getPropertyId(Address address) throws SQLException {
	def propertyId
	
	stmt = """select id
              from property 
              where address_id = ${address.id}
		   """
	try {
		row = dbConnection.firstRow(stmt)
		if (row) {
			propertyId = row.id
		} else {
			propertyId = null
		}
	} catch (SQLException sqlex) {
		println("Got SQL exception selecting id from address with fullAddress of : " + address.fullAddress + "\n" + sqlex.getMessage() + "\n\n" )
		totalSQLExceptions++
		throw sqlex
	}
	return propertyId
}

def boolean addressExists(Address myAddress) throws SQLException {
	try {
	    def addressId = getAddressId(myAddress)
	    if (addressId) { return true } else { return false }
	} catch (SQLException ex) {
		println("addressExists(): Got exception " + ex.getMessage() + "\n\n" )
		throw ex
	}
}

def getPropertyId(dbConnection, property) throws SQLException {
	def row
	
	stmt = """select id 
              from property 
              where address_id = ${property.addressId}
		   """
	try {
		row = dbConnection.firstRow(stmt)
	} catch (SQLException sqlex) {
		println("getPropertyId(): Got exception selecting property id from property table: " + ex.getMessage() + "\n\n" )
		totalSQLExceptions++
		throw sqlex
	}
	def propertyId = row.id
	return propertyId
}

def getAddress(Address address, HtmlPage detailsPage) {
	try {
		def myParser = getParser(detailsPage)
		def addressText = ""
		def street = ""
		def comma  = ""
		def cityStateZip = ""
		def zipCode = ""
		def city = ""
		def state = ""
		def matcher = ""
		def firstLine = false
		def stuff
		def counter = 0
		
		myParser.'**'.findAll{ it.@id == 'property-information'}.each{ item ->
			addressText = item.div.h1.text()
			addressText = addressText.toString().replaceAll("\\p{C}", "|")  			// replace weird control chars that result from <BR> with pipe sign
			
			def addressParts = splitIt(addressText, "|")
			counter = 0
			def numparts = addressParts.size
			addressParts.each {
				if (it =~ /.*[a-zA-Z]+.*/) {
					//it = 'Kailua Kona HI, 96740' //uncomment to test specific addresses
					if (it =~ /,?\s*[A-Z]{2},?\s+[0-9]{5,}-?[0-9]{0,4}\s*$/){
						cityStateZip = it.trim()
					}else{
						street = it.trim()
					}
				}
			}
			address.fullAddress = street + " " + cityStateZip
			address.streetName = street

			if (cityStateZip =~ /,+\s*[A-Z]{2}\s+[0-9]{5,}-?[0-9]{0,4}\s*$/){
				addressParts = splitIt(cityStateZip, ",")
				city = addressParts[0]
				(state, zipCode) = splitIt(addressParts[1], " ")
			} else if (cityStateZip =~ /\s*[A-Z]{2},+\s+[0-9]{5,}-?[0-9]{0,4}\s*$/){
				addressParts = splitIt(cityStateZip, ",")
				stuff = addressParts[0] =~ /\s*[A-Z]{2}\s*$/
                state = stuff[0]
				city = addressParts[0].toString().replaceAll(/s*[A-Z]{2}\s*$/, "")
				zipCode = addressParts[1]
			}
			address.city = city.trim()
			address.state = state.trim()
			
			if (zipCode =~ /-/) {														// If there's a '-' then break into zip and zip+4, else just zip
				address.zipCode = splitIt(zipCode, "-")[0]
				address.zipPlusFour = zipCode.trim()
			} else {
				address.zipCode = zipCode.trim()
			}
			address.county = "Hawaii"
		}
	} catch (Exception e) {
		showException("getAddress", e, "n", "no")
	}
	println("The address is ${address}")
	return address
}

def getPropertyType(HtmlPage page) { // Should be one of Land, Single-Family or Condo
	def propertyType = ""
	def item = page.getByXPath("//tr[th='TYPE']")
	
	item = page.getByXPath("//td")
	propertyType = item[0].asText().trim()
	
	// There could be two types seperated by commas, ie, Land,Residential, Land,Commercial, if Residential then change from Land to Home.
	if (propertyType =~ /,/) {
			if ((propertyType =~ /(?i)Land/) && (propertyType =~ /(?i)Residential/)) { propertyType = "Residential" }
			if ((propertyType =~ /(?i)Land/) && (propertyType =~ /(?i)Commercial/)) { propertyType = "Land" }
	}
	return propertyType
}

def getFeatures(groovy.util.slurpersupport.GPathResult myParser) { // Should be one of Land, Single-Family or Condo
	def result = ""
	myParser.'**'.findAll{ it.@id == 'features-list'}.each{ list ->
		def items = list.li
		items.each{
			def text = it.text().trim()
			result = result + text + "|"
		}
	}
	return result
}

def getUlLinks(groovy.util.slurpersupport.GPathResult myParser) { // Should be one of Land, Single-Family or Condo
	def result = ""
	myParser.'**'.findAll{ it.name() == 'li'}.each{ link ->
		def text = link.text().trim()
		result = result + text + ", "
	}
	def myRegEx = /^(.*?)(\,+)(\s?)$/             //non-greedy search for chars then greedy search for '|' sign at end of string.
	
	def myMatcher = ( result =~ myRegEx )

	if (myMatcher.matches()) {
		result = myMatcher[0][1]
	}
	result = convertIt(result, "String")

	return result
}

def getScrapedProperties(String type, Property props, HtmlPage page){
	def count = 0
	def myParser = getParser(page)
	def marker
	def heading
	
	myParser.'**'.findAll{ it.@id == 'details_tab'}.each{ item ->
		item.'**'.findAll{ it.name() == 'div' && it.@class == 'span4'}.each {

			def headers = it.table.tbody.tr.th  					//Get all th elements under <tr>
			def cells =   it.table.tbody.tr.td  					//Get all td elements under <tr>
			headers.each {
				marker = it.text().trim()
println("The heading is ${marker}")
				switch (marker) {
					case ~/STATUS/:	// Home and Condo
						try {
							props.status = convertIt(cells[count].toString().trim(),"String")
						} catch (Exception ex) {
							showException("getHomeProperties.Status:Other", ex, 'n', null)
						}
						println("The status is |${props.status}|")
					break

					case ~/BEDROOMS/: // Home and Condo
						try {
							props.beds = convertIt(cells[count].toString().trim(),"int")
						} catch (NumberFormatException e) {
							showException("getHomeProperties.beds:BadNumberFormat", e, 'n', null)
						} catch (Exception ex) {
							showException("getHomeProperties.beds:Other", ex, 'n', null)
						}
						println("The beds is |${props.beds}|")
					break
					
					case ~/BATHROOMS/: // Home and Condo
						try {
							props.baths = convertIt(cells[count].toString().trim(),"int")
						} catch (NumberFormatException e) {
							showException("getHomeProperties.baths:BadNumberFormat", e, 'n', null)
						} catch (Exception ex) {
							showException("getHomeProperties.baths:Other", ex, 'y', null)
						}
						println("The baths is |${props.baths}|")
					break

					case ~/HALF BATHS/: // Home only
						try {
							props.halfbaths = convertIt(cells[count].toString().trim(),"int")
						} catch (NumberFormatException e) {
							showException("getHomeProperties.baths:BadNumberFormat", e, 'n', null)
						} catch (Exception ex) {
							showException("getHomeProperties.baths:Other", ex, 'y', null)
						}
						println("The halfbaths is |${props.halfbaths}|")
					break

					case ~/LAND AREA/:	// Home and Land	
						try {
							def landArea = convertIt(cells[count].toString().trim(),"String")
							def lotSize = landArea.grep(~/\d*\.*\d*/)
							def units = landArea.grep(~/[A-Za-z]+/)
							def lotSizeUnits = units.join('')
							landArea = lotSize.join('')
							props.lotSize = new Double(landArea)
							props.lotSizeUnits = lotSizeUnits
						} catch (NumberFormatException e) {
							showException("getHomeProperties.lotSize|lotSizeUnits:BadNumberFormat", e, 'n', null)
						} catch (Exception ex) {
							showException("getHomeProperties.lotSize:Other", ex, 'y', null)
						}
						println("The lotSize is |${props.lotSize}|")
						println("The lotSizeUnits is |${props.lotSizeUnits}|")
					break
					
					case ~/LIVING AREA/:  // Home and Condo
						try {
							def houseSize = convertIt(cells[count].toString().trim(), "String")
							houseSize = houseSize.grep(~/\d+/)
							//def units = houseSize.grep(~/[A-Za-z]+/)
							props.houseSize = new Integer(houseSize.join('')).intValue()
						} catch (NumberFormatException e) {
							showException("getHomeProperties.houseSize:BadNumberFormat", e, 'n', null)
						} catch (Exception ex) {
							showException("getHomeProperties.houseSize:Other", ex, 'y', null)
						}
						println("The houseSize is |${props.houseSize}|")
					break
					
					case ~/DISTRICT/:  // Home and Condo
						try {
							props.district = convertIt(cells[count].toString().trim(),"String")
						} catch (Exception ex) {
							showException("getHomeProperties.District:Other", ex, 'y', null)
						}
						println("The district is |${props.district}|")
					break
					
					case ~/Subdivision/:  // Home and Condo
						try {
							props.subDivision = convertIt(cells[count].toString().trim(),"String")
						} catch (Exception ex) {
							showException("getHomeProperties.subDivision:Other", ex, 'y', null)
						}
						println("The subDivision is |${props.subDivision}|")
					break

					case ~/Condo Project/:  // Condo only
						try {
							props.condoProject = convertIt(cells[count].toString().trim(),"String")
						} catch (Exception ex) {
							showException("getHomeProperties.condoProject:Other", ex, 'y', null)
						}
						println("The condoProject is |${props.condoProject}|")
					break
					
					case ~/Features/:  // Home and Condo
						try {
							props.features = getUlLinks(cells[count])
						} catch (Exception ex) {
							showException("getHomeProperties.Features:Other", ex, 'y', null)
						}
						println("The features is |${props.features}|")
					break
				}
				count++
			} // end headers.each
		} // end item.findAll
	}

    // Get the "description" field... All types
    myParser.'**'.findAll{ it.name() == 'div' && it.@class == 'tab-content-section'}.each{ aDiv ->
		marker = aDiv.h2.text().trim()
		if (marker == 'Property Description') {
			props.description = aDiv.p.text().trim()
			props.description = props.description.replaceAll(/(\s{2,})/, "")
			if (props.description.length() > property_desc_length) {
				StringBuffer myBuffer = new StringBuffer(props.description)
				myBuffer.setLength(property_desc_length)
				props.description = myBuffer.toString()
			}
		}
	}
	println("The description is |${props.description}|")

	// Get the price...  All types
	myParser.'**'.findAll{ it.name() == 'h2' && it.@id == 'price'}.each{ item ->
		def price = convertIt(item.text().trim(), "BigDecimal")
		props.price = price.setScale(2)
		println("The price is |${props.price}|")
	}

	// Get the mls...  All types
	myParser.'**'.findAll{ it.name() == 'h3' && it.@id == 'property-id'}.each{ item ->
		item = item.toString().replaceAll(/MLS /, "")
		def mls = convertIt(item.trim(), "String")
		props.mls = mls
		println("The mls is |${props.mls}|")
	}

    //if (type == "Condo" || type == "Residential") {
    myParser.'**'.findAll{ it.name() == 'div' && it.@class == 'tab-content-section'}.each{ aDiv ->
		heading = aDiv.h2.text().trim()
println("The heading is |${heading}|")
		switch (heading) {
			case ~/Property Details/:
println("The type is |${type}|")
				def headers = aDiv.div.div.table.tbody.tr.th  					//Get all th elements under <tr>
				def cells =   aDiv.div.div.table.tbody.tr.td  					//Get all td elements under <tr>
				count = 0
				headers.each {
					marker = it.text().trim()
println("The heading is ${marker}")
					switch (marker) {
						case ~/(?i)DAYS ON MARKET/:  // Home and Condo
						//case ~/Days On Market/:  // Home and Condo
							try {
								props.daysOnMkt = convertIt(cells[count].toString().trim(),"Integer")
							} catch (NumberFormatException e) {
								showException("getHomeProperties.daysOnMkt:BadNumberFormat", e, 'n', null)
							} catch (Exception ex) {
								showException("getHomeProperties.daysOnMkt:Other", ex, 'y', null)
							}
							println("The daysOnMkt is |${props.daysOnMkt}|")
						break

						case ~/(?i)ORIGINAL LIST PRICE/:  // Home and Condo
							try {
								props.originalListPrice = (convertIt(cells[count].toString().trim(),"BigDecimal")).setScale(2)
							} catch (NumberFormatException e) {
								showException("getHomeProperties.originalListPrice:BadNumberFormat", e, 'n', null)
							} catch (Exception ex) {
								showException("getHomeProperties.originalListPrice:Other", ex, 'y', null)
							}
							println("The originalListPrice is |${props.originalListPrice}|")
						break

						case ~/(?i)PRICE CHANGE/:  // Home and Condo
							try {
								def priceChange = cells[count].text().replaceAll(/\(.*\)/, "")
								props.priceChange = convertIt(priceChange.trim(),"BigDecimal").setScale(2)
							} catch (NumberFormatException e) {
								showException("getHomeProperties.priceChange:BadNumberFormat", e, 'n', null)
							} catch (Exception ex) {
								showException("getHomeProperties.priceChange:Other", ex, 'y', null)
							}
							println("The priceChange is |${props.priceChange}|")
						break

						case ~/(?i)PRICE PER SQUARE FOOT/:  // Home and Condo
							try {
								props.pricePerSqFt = convertIt(cells[count].toString().trim(),"BigDecimal").setScale(2)
							} catch (NumberFormatException e) {
								showException("getHomeProperties.pricePerSqFt:BadNumberFormat", e, 'n', null)
							} catch (Exception ex) {
								showException("getHomeProperties.pricePerSqFt:Other", ex, 'y', null)
							}
							println("The pricePerSqFt is |${props.pricePerSqFt}|")
						break

						case ~/(?i)ASSESSED VALUE/:  // Home and Condo
							try {
								props.assessedValue = convertIt(cells[count].toString().trim(),"BigDecimal").setScale(2)
							} catch (NumberFormatException e) {
								showException("getHomeProperties.AssessedValue:BadNumberFormat", e, 'n', null)
							} catch (Exception ex) {
								showException("getHomeProperties.AssessedValue:Other", ex, 'y', null)
							}
							println("The assessedValue is |${props.assessedValue}|")
						break

						case ~/(?i)ANNUAL PROPERTY TAX/:  // Home and Condo
							try {
								props.propertyTax = convertIt(cells[count].toString().trim(),"BigDecimal")setScale(2)
							} catch (NumberFormatException e) {
								showException("getHomeProperties.propertyTax:BadNumberFormat", e, 'n', null)
							} catch (Exception ex) {
								showException("getHomeProperties.PropertyTax:Other", ex, 'y', null)
							}
							println("The propertyTax is |${props.propertyTax}|")
						break

						case ~/Tax Key \(TMK\)/:  // Home and Condo
							try {
								props.taxKey = convertIt(cells[count].toString().trim(),"String")
							} catch (Exception ex) {
								showException("getHomeProperties.taxKey:Other", ex, 'y', null)
							}
							println("The taxKey is |${props.taxKey}|")
						break

						case ~/SHORT SALE/:  // Home and Condo
							try {
								if (cells[count].toString().trim() ==~ /(?i)No/) props.shortSale = new Double("0")
								else props.shortSale = new Double("1")
							} catch (NumberFormatException e) {
								showException("getHomeProperties.shortSale:BadNumberFormat", e, 'n', null)
							} catch (Exception ex) {
								showException("getHomeProperties.shortSale:Other", ex, 'y', null)
							}
							println("The shortSale is |${props.shortSale}|")
						break

						case ~/PRE-FORECLOSURE/:  // Home and Condo
							try {
								marker = cells[count].toString().trim()
								if (cells[count].toString().trim() ==~ /(?i)No/) props.preForeclosure = new Double("0")  //Ignore case (case insensitive)
								else props.preForeclosure = new Double("1")
							} catch (NumberFormatException e) {
								showException("getHomeProperties.preForeclosure:BadNumberFormat", e, 'n', null)
							} catch (Exception ex) {
								showException("getHomeProperties.preForeclosure:Other", ex, 'y', null)
							}
							println("The preForeclosure is |${props.preForeclosure}|")
						break

						case ~/REO\/LENDER/:  // Home and Condo
							try {
								props.reo = convertIt(cells[count].toString().trim(),"String")
							} catch (Exception ex) {
								showException("getHomeProperties.reo:Other", ex, 'y', null)
							}
							println("The reo is |${props.reo}|")
						break

						case ~/(?i)TENURE/:  // Home and Condo
							try {
								props.tenure = convertIt(cells[count].toString().trim(),"String")
							} catch (Exception ex) {
								showException("getHomeProperties.tenure:Other", ex, 'y', null)
							}
							println("The tenure is |${props.tenure}|")
						break

						case ~/Condo Maintenance/:  // Condo only
							try {
								def fee = cells[count].toString().trim().grep(~/\d|\./).join("")
								props.maintenanceFee = convertIt(fee.toString().trim(),"BigDecimal")
							} catch (NumberFormatException e) {
								showException("getHomeProperties.maintenanceFee:BadNumberFormat", e, 'n', null)
							} catch (Exception ex) {
								showException("getHomeProperties.maintenanceFee:Other", ex, 'y', null)
							}
							println("The maintenanceFee is |${props.maintenanceFee}|")
						break

						// Association Due
						case ~/Association Due/:  // Home and Condo
							try {
								props.hoaDuesPerMonth = getAssociationDues(cells[count].toString().trim()) //BigDecimal
							} catch (Exception ex) {
								showException("getHomeProperties.hoaDuesPerMonth:Other", ex, 'y', null)
							}
							println("The hoaDuesPerMonth is |${props.hoaDuesPerMonth}|")
						break
					}
					count++
				}  //End headers.each()
			break

			case ~/Interior/:
				def headers = aDiv.table.tbody.tr.th  				//Get all th elements under <tr>
				def cells =   aDiv.table.tbody.tr.td  			//Get all ul elements under <tr>
				count = 0
				headers.each {
					marker = it.text().trim()
println("The heading is ${marker}")
					switch (marker) {
						case ~/Flooring/:  // Condo only
							try {
								props.floorCovering = getUlLinks(cells[count])
							} catch (Exception ex) {
								showException("getHomeProperties.floorCovering:Other", ex, 'y', null)
							}
							println("The floorCovering is |${props.floorCovering}|")
						break

						case ~/Appliances/:  // Home and Condo
							try {
								props.appliances = getUlLinks(cells[count])
							} catch (Exception ex) {
								showException("getHomeProperties.appliances:Other", ex, 'y', null)
							}
							println("The appliances is |${props.appliances}|")
							break

						case ~/Washer\/Dryer/:  // Home and Condo
							def washerDryer = ""
							try {
								washerDryer = getUlLinks(cells[count])
								props.appliances = props.appliances + ", " + washerDryer
							}  catch (Exception ex) {
								showException("getHomeProperties.washerDryer:Other", ex, 'y', null)
							}
							
							println("The applicance plus washerDryer is |${props.appliances}|")
						break

						case ~/Window Coverings/:  // Condo only
							def windowCoverings = ""
							try {
								//windowCoverings = convertIt(cells[count].ul.li.text().trim(),"String")
								props.windowCoverings = getUlLinks(cells[count])
								//props.windowCoverings = windowCoverings
							} catch (Exception ex) {
								showException("getHomeProperties.windowCoverings:Other", ex, 'y', null)
							}
							
							println("The windowCoverings is |${props.windowCoverings}|")
						break
					}
					count++
				}  //End headers.each()
			break

			case ~/Exterior/:
				def headers = aDiv.div.div.table.tbody.tr.th  			//Get all th elements under <tr>
				def cells =   aDiv.div.div.table.tbody.tr.td  			//Get all ul elements under <tr>
				count = 0
				def text = ""

				headers.each {
					marker = it.text().trim()
println("The heading is ${marker}")
					switch (marker) {
						case ~/Zoning/:  // Home only
							try {
								props.zoning = getUlLinks(cells[count])
							} catch (NumberFormatException e) {
								showException("getHomeProperties.zoning:BadNumberFormat", e, 'n', null)
							} catch (Exception ex) {
								showException("getHomeProperties.zoning:Other", ex, 'y', null)
							}
							println("The zoning is |${props.zoning}|")
						break

						case ~/(?i)Flood Zone/:  // Home only
							try {
								text = getUlLinks(cells[count])
								if (text == 'X') props.floodZone = (new Double("1")).intValue()
							   	else props.floodZone = (new Double("0")).intValue()
							} catch (NumberFormatException e) {
								showException("getHomeProperties.floodZone:BadNumberFormat", e, 'n', null)
							} catch (Exception ex) {
								showException("getHomeProperties.floodZone:Other", ex, 'y', null)
							}
							println("The floodZone is |${props.floodZone}|")
						break
					}
					count++
				}

				headers = aDiv.table.tbody.tr.th
				cells =   aDiv.table.tbody.tr.td
				count = 0
				headers.each {
					marker = it.text().trim()
println("The heading is ${marker}")
					switch (marker) {
						case ~/Parking/:  // Home and Condo
							def parkingType = ""
							try {
								parkingType = getUlLinks(cells[count])
							} catch (Exception ex) {
								showException("getHomeProperties.parkingType:Other", ex, 'y', null)
							}
							props.parkingType = parkingType 
							println("The parkingType is |${props.parkingType}|")
						break

						case ~/Design/:  // Home and Condo
							try {
								props.design = getUlLinks(cells[count])
							} catch (Exception ex) {
								showException("getHomeProperties.design:Other", ex, 'y', null)
							}
							println("The design is |${props.design}|")
						break

						case ~/Topography/:  // Home and Condo
							try {
								props.topography = getUlLinks(cells[count])
							}  catch (Exception ex) {
								showException("getHomeProperties.topography:Other", ex, 'y', null)
							}
							println("The topography is |${props.topography}|")
						break

						case ~/(?i)Lot Description/:  // Home and Condo
							try {
								props.lotDesc = getUlLinks(cells[count])
							} catch (Exception ex) {
								showException("getHomeProperties.lotDesc:Other", ex, 'y', null)
							}
							println("The lotDesc is |${props.lotDesc}|")
						break

						case ~/Frontage/:  // Home only
							try {
								props.frontage = getUlLinks(cells[count])
							} catch (NumberFormatException e) {
								showException("getHomeProperties.frontage:BadNumberFormat", e, 'n', null)
							} catch (Exception ex) {
								showException("getHomeProperties.frontage:Other", ex, 'y', null)
							}
							println("The frontage is |${props.frontage}|")
						break

						case ~/Fencing/:  // Home only
							try {
								props.fencing = getUlLinks(cells[count])
							} catch (NumberFormatException e) {
								showException("getHomeProperties.fencing:BadNumberFormat", e, 'n', null)
							} catch (Exception ex) {
								showException("getHomeProperties.fencing:Other", ex, 'y', null)
							}
							println("The fencing is |${props.fencing}|")
						break
					}
					count++
				}  //End headers.each()
			break	// End of Exteriors features

			case ~/Building Features/:
	println("The Building heading is ${heading}")
				def headers = aDiv.table.tbody.tr.th  			//Get all th elements under <tr>
				def cells =   aDiv.table.tbody.tr.td  			//Get all ul elements under <tr>
				count = 0
				def text = ""

				headers.each {
					marker = it.text().trim()
println("The heading is ${marker}")
					switch (marker) {
						case ~/Year Built/:  // Home and Condo
							try {
								props.yearBuilt = convertIt(cells[count].text().trim(), "Timestamp")
							} catch (NumberFormatException e) {
								showException("getHomeProperties.yearBuilt:BadNumberFormat", e, 'n', null)
							} catch (Exception ex) {
								showException("getHomeProperties.yearBuilt:Other", ex, 'y', null)
							}
							println("The yearBuilt is |${props.yearBuilt}|")
						break

						case ~/Roof Design/:  // Home only
							try {
								props.roofType = getUlLinks(cells[count])
							} catch (NumberFormatException e) {
								showException("getHomeProperties.roofType:BadNumberFormat", e, 'n', null)
							} catch (Exception ex) {
								showException("getHomeProperties.roofType:Other", ex, 'y', null)
							}
							println("The roofType is |${props.roofType}|")
						break

						case ~/Driveway/:  // Home only
							try {
								props.driveway = getUlLinks(cells[count])
							} catch (NumberFormatException e) {
								showException("getHomeProperties.driveway:BadNumberFormat", e, 'n', null)
							} catch (Exception ex) {
								showException("getHomeProperties.driveway:Other", ex, 'y', null)
							}
							println("The driveway is |${props.driveway}|")
						break

						case ~/Other Features/:  // Home and Condo
							try {
								props.otherFeatures = getUlLinks(cells[count])
							} catch (Exception ex) {
								showException("getHomeProperties.otherFeatures:Other", ex, 'y', null)
							}
							println("The otherFeatures is |${props.otherFeatures}|")
						break

						case ~/Water Heater/:  // Home and Condo
							try {
								props.appliances = props.appliances + ", " + "Water Heater = " + getUlLinks(cells[count])
							} catch (Exception ex) {
								showException("getHomeProperties.appliances:Other", ex, 'y', null)
							}
							println("The appliances plus water heater is |${props.appliances}|")
						break

						case ~/Kitchen/:  // Condo only
							try {
								props.kitchenType = getUlLinks(cells[count])
							} catch (Exception ex) {
								showException("getHomeProperties.kitchenType:Other", ex, 'y', null)
							}
							println("The kitchenType is |${props.kitchenType}|")
						break

						case ~/Countertops/:  // Condo only
							try {
								props.counterTops = getUlLinks(cells[count])
							} catch (Exception ex) {
								showException("getHomeProperties.counterTops:Other", ex, 'y', null)
							}
							println("The counterTops is |${props.counterTops}|")
						break

						case ~/HEATING\/COOLING/:  // Condo only
							try {
								props.heatingCooling = getUlLinks(cells[count])
							} catch (Exception ex) {
								showException("getHomeProperties.heatingCooling:Other", ex, 'y', null)
							}
							println("The heatingCooling is |${props.heatingCooling}|")
						break
					}
					count++
				}  //End headers.each()
			break

			case ~/Utilities/:
	println("The Utilities heading is ${heading}")
				def headers = aDiv.table.tbody.tr.th  			//Get all th elements under <tr>
				def cells =   aDiv.table.tbody.tr.td  			//Get all ul elements under <tr>
				count = 0
				def text = ""

				headers.each {
					marker = it.text().trim()
println("The heading is ${marker}")
					switch (marker) {
						case ~/POWER/:  // Home and Condo
							try {
								props.power = getUlLinks(cells[count])
							} catch (NumberFormatException e) {
								showException("getHomeProperties.power:BadNumberFormat", e, 'n', null)
							} catch (Exception ex) {
								showException("getHomeProperties.power:Other", ex, 'y', null)
							}
							println("The power is |${props.power}|")
						break

						case ~/WATER/:  // Home and Condo
							try {
								props.water = getUlLinks(cells[count])
							} catch (NumberFormatException e) {
								showException("getHomeProperties.water:BadNumberFormat", e, 'n', null)
							} catch (Exception ex) {
								showException("getHomeProperties.water:Other", ex, 'y', null)
							}
							println("The water is |${props.water}|")
						break
 
						case ~/WASTEWATER/:  // Home and Condo
							try {
								props.sewer = getUlLinks(cells[count])
							} catch (NumberFormatException e) {
								showException("getHomeProperties.sewer:BadNumberFormat", e, 'n', null)
							} catch (Exception ex) {
								showException("getHomeProperties.sewer:Other", ex, 'y', null)
							}
							println("The sewer is |${props.sewer}|")
						break

						case ~/TELEPHONE/:  // Home and Condo
							try {
								props.phoneSvc = getUlLinks(cells[count])
							} catch (NumberFormatException e) {
								showException("getHomeProperties.phoneSvc:BadNumberFormat", e, 'n', null)
							} catch (Exception ex) {
								showException("getHomeProperties.phoneSvc:Other", ex, 'y', null)
							}
							println("The phoneSvc is |${props.phoneSvc}|")
						break

						case ~/TV/:  // Home and Condo
							try {
								props.tvSvc = getUlLinks(cells[count])
							} catch (NumberFormatException e) {
								showException("getHomeProperties.tvSvc:BadNumberFormat", e, 'n', null)
							} catch (Exception ex) {
								showException("getHomeProperties.tvSvc:Other", ex, 'y', null)
							}
							println("The tvSvc is |${props.tvSvc}|")
						break

						case ~/INTERNET/:   // Home and Condo
							try {
								props.internetSvc = getUlLinks(cells[count])
							} catch (NumberFormatException e) {
								showException("getHomeProperties.internetSvc:BadNumberFormat", e, 'n', null)
							} catch (Exception ex) {
								showException("getHomeProperties.internetSvc:Other", ex, 'y', null)
							}
							println("The internetSvc is |${props.internetSvc}|")
						break

						case ~/SOLID WASTE DISPOSAL/:  // Condo only
							try {
								props.solidWasteDisposal = getUlLinks(cells[count])
							} catch (Exception ex) {
								showException("getHomeProperties.solidWasteDisposal:Other", ex, 'y', null)
							}
							println("The solidWasteDisposal is |${props.solidWasteDisposal}|")
						break
					}
					count++
				}  //End headers.each()
			break

			default: 
				println("The UNKNOWN HEADING ...................................................................................................... is: |${marker}|")
			break
		} //End switch(marker)
	}  //End each{ div with class='tab-content-section' }
	return props
}  // End of getScrapedProperties

def getAssociationDues(String text) {
	def amount = text.grep(~/\d*\.*\d*/).join('')
	def term = text.grep(~/[A-Za-z]+/).join('')
	BigDecimal zero = (new BigDecimal("0")).setScale(3, BigDecimal.ROUND_HALF_UP)
	BigDecimal fees

    try {
		fees = convertIt(amount, "BigDecimal")
		fees = fees.setScale(2, BigDecimal.ROUND_HALF_UP)

		if (term ==~ /(?i)year.*/) {
			fees = fees.divide(new BigDecimal("12.00").setScale(2), BigDecimal.ROUND_HALF_UP).setScale(2)
		}
	} catch (NumberFormatException e) {
		showException("getAssociationDues.hoaDuesPerMonty:BadNumberFormat", e, 'n', null)
		return zero
	}

	fees = fees.setScale(2, BigDecimal.ROUND_HALF_UP)
	return fees
}

def getThumbs(HtmlPage photoPage, List<Image> pics) {
	def bigPicURI = ""
	HtmlPage newPage
	HtmlPage bigPicPage
	File dirPath
	File thumbPath
	File bigPicPath
	File picFile
	File bigPicFile
	HtmlImage thePic
	Image myImage
	int success
	long timeInMilliseconds
	int index = 0
	// TODO:  Change file paths to URIs for portability
	
	// Clean out the pics directory:
	try {
		dirPath = new File(Globals.picsDir)
		FileUtils.deleteRecursive(dirPath)
	} catch (FileNotFoundException fnfex) {
		println("getThumbs:  Encountered FileNotFoundException -- Could not rm -r ${Globals.picsDir}?:  \n" + "\t\t" + fnfex.getMessage())
		totalIOExceptions++
		throw fnfex
	} catch (Exception ex) {
		println("getThumbs:  Fatal exception, file perms trying to delete?:  \n" + "\t\t" + ex.getMessage())
		totalIOExceptions++
		throw ex
	}
	
	// Create C:/temp/pics, C:/temp/pics/thumbs, C:/temp/pics/bigPics directories 
	try {
		thumbPath = new File(Globals.thumbsDir)
		bigPicPath = new File(Globals.bigPicsDir)
		dirPath.mkdir()
		dirPath.setWritable(true, true)
		thumbPath.mkdir()
		thumbPath.setWritable(true, true)
		bigPicPath.mkdir()
		bigPicPath.setWritable(true, true)
	} catch (Exception ex) {
		println("getThumbs:  Fatal exception, problem creating directories under ${Globals.picsDir}?:  \n" + "\t\t" + ex.getMessage())
		throw ex
	}
	
    def photoTab = photoPage.getByXPath("//a[@href='#photos_tab']")
	newPage = photoTab[0].click()
	def thumbs = newPage.getByXPath("//img[@width='179']")
	//def thumbs = thumbsSection.getByXPath("//img[@width='179']")

	try {
		thumbs.each { pic ->
			
			picFile = savePicToFile("thumb", index, pic) 
			bigPicFile = savePicToFile("big", index, pic)
			if (picFile && bigPicFile){
				myImage = new Image()
				myImage = setImageDimensions("thumb", myImage, pic)
				def atts = splitIt(pic.getSrcAttribute(), "/")
				myImage.size = picFile.length()
				myImage.content = picFile.bytes
				//myImage.content = 0
				myImage.dateCreated = new java.sql.Timestamp((new java.util.Date()).getTime())
				myImage.lastUpdated = new java.sql.Timestamp((new java.util.Date()).getTime())
				myImage.updateUser = "script"
				myImage.type = "thumb"
				//println("The image is ${myImage}")
				pics.add(myImage)
				
				myImage = new Image()
				myImage = setImageDimensions("big", myImage, pic)
				myImage.size = bigPicFile.length()
				myImage.content = bigPicFile.bytes
				//myImage.content = 0
				myImage.dateCreated = new java.sql.Timestamp((new java.util.Date()).getTime())
				myImage.lastUpdated = new java.sql.Timestamp((new java.util.Date()).getTime())
				myImage.updateUser = "script"
				myImage.type = "large"
				//println("The image is ${myImage}")
				pics.add(myImage)
				timeInMilliseconds = 1000 * getSleepPeriod(1,3)
				sleep(timeInMilliseconds)
				index++
			}
		}
	} catch (UnsupportedOperationException uoex) {
		// TODO: Change this so it sends an email....
		println("getThumbs:  Encountered UnsupportedOperationException:  " + uoex.getMessage())
		totalIOExceptions++
		throw uoex
	} catch (FileAlreadyExistsException faeex) {
		// TODO: Change this so it sends an email....
		println("getThumbs:  Encountered FileAlreadyExistsException:  " + faeex.getMessage())
		totalIOExceptions++
		throw faeex
	} catch (IOException ioex) {
		// TODO: Change this so it sends an email....
		println("getThumbs:  Encountered IOException:  " + ioex.getMessage())
		totalIOExceptions++
		throw ioex
	} catch (SecurityException  sex) {
		// TODO: Change this so it sends an email....
		println("getThumbs:  Encountered SecurityException:  " + sex.getMessage())
		totalIOExceptions++
		throw sex
	} /*
	catch (ImageSaveException  isex) {
		// TODO: Change this so it sends an email....
		println("getThumbs:  Encountered ImageSaveException:  " + isex.getMessage())
		totalIOExceptions++
		throw isex
	} 
	*/
	//println("Got ${totalIOExceptions} IOException......")
	return pics
}

def File savePicToFile(String picSizeType, int index, Object details) {
	String srcURI = ""
	String bigPicURI = ""
	String myId = ""
	//ArrayList<HtmlImage> picList
	String fileName = ""
	def atts
	def listSize
	File myPic
	//HtmlImage pic
	String errorMsg = ""
	
	try {
		def pic
		if (picSizeType == "big") {
			pic = (HtmlImage) details
			srcURI = pic.getSrcAttribute()
			atts = splitIt(srcURI, "/")
			//bigPicURI = '<img src="http://diuldeadj4dpx.cloudfront.net/pics/property/35040606/7/IDX_7/v0//maxheight/503" alt="image" style="position: absolute; top: 52px; left: 685px; width: auto; height: auto;">'
			bigPicURI =             "http://diuldeadj4dpx.cloudfront.net/pics/property/${atts[4]}/${atts[5]}/IDX_${atts[5]}/v0//maxheight/503"
			println("The uri is: ${bigPicURI}")
			fileName = getPicFileName(picSizeType, bigPicURI)
			myPic = new File(fileName)
			myPic << new URL(bigPicURI).openStream()
			/*
			def picList = (ArrayList<HtmlImage>) details
			pic = getBigPicFromList(index, picList)
			myId = pic.getId()
			if (myId == "NADA") {
				// Don't save it....
			} else {  
				srcURI = pic.getSrcAttribute()
				fileName = getPicFileName(picSizeType, srcURI)
			}
			*/
		} else {
			pic = (HtmlImage) details
	    	srcURI = pic.getSrcAttribute()
			fileName = getPicFileName(picSizeType, srcURI)
			myPic = new File(fileName)
			pic.saveAs(myPic)
		}

	} catch (Exception ex) {
	    errorMsg = "savePicToFile: Got error trying to save pic ${fileName} to file -- " + ex.getMessage() + "... Continuing...."
		println(errorMsg)
		return null
		//throw new ImageSaveException(errorMsg)
	}
	
	return myPic
}

def HtmlImage getBigPicFromList(int index, ArrayList<HtmlImage> bigPics) {
	def matcher 
	def count = 0
	int matches = 0
	HtmlImage bigPic
	//HtmlImage bigPic
	def listString = bigPics.toListString()
	//println("The bigPics list is: " + listString)
	
    bigPics.any { 
		count++
		println("The count is: " + count)
		println("The it is: " + it.toString())
		//String src = it.getClass().toString()
		bigPic = it
		String src = bigPic.getSrcAttribute()
		println("The src is: " + src)
		//matcher = src =~ /.*pics\/property\/[0-9]+\/$index\/IDX_$index.*\/\/maxwidth\/600\/maxheight\/400\/matte.*/
		// /pics\/property\/[0-9]+\/$index\/IDX_$index\/v1\/\/maxwidth\/[0-9]+\/maxheight\/[0-9]+\/matte\/!fff/
		// src="http://diuldeadj4dpx.cloudfront.net/pics/property/18149417/0/IDX_0/v1//maxheight/380/maxwidth/572/matte/!fff/"
		matcher = src =~ /pics\/property\/[0-9]+\/$index\/IDX_$index\/v1\/\/maxheight\/[0-9]+\/maxwidth\/[0-9]+\//
		matches = matcher.getCount()
		if (matches > 0) {
			true
		}
		// Example: HtmlImage[<img src="http://diuldeadj4dpx.cloudfront.net/pics/property/18149417/0/IDX_0/v0//maxwidth/600/maxheight/400/matte/!FFF" alt="75-6081 ALII DR, Kailua Kona, HI, 96740 - Image 1">]
		//matcher = bigPic.getSrcAttribute() =~ /[A-Z]{2},/										// NOTE: *** Best way to do matching...
		//state = matcher[0]
	}
	if (matches) {
		return bigPic
	} else {
	    bigPic = new HtmlImage()
		bigPic.setId("NADA")
	    return bigPic
	}
	
}

def Image setImageDimensions(String type, Image myImage, HtmlImage pic) {
	def atts
	def listSize
	def sizes
	def widthHeight
    def width
	def height
	
	atts = splitIt(pic.getSrcAttribute(), "/")
	if (atts) {
		// Need to hardcode this since info is not in URI for big pics
		if (type == "big") {
			myImage.width = new Integer("600").intValue()
			myImage.height = new Integer("400").intValue()
		} else {
			listSize = atts.size()
			sizes = atts[listSize - 1]
			widthHeight = splitIt(sizes, ",")
			myImage.width = new Integer(widthHeight[0]).intValue()
			myImage.height = new Integer(widthHeight[1]).intValue()
		}
	}
	return myImage
}

def String getPicFileName(String picType, String src) throws Exception {
	def atts
	def listSize
	def filePath
	String fileName
	def fileNameStub
	
	atts = splitIt(src, "/")
	if (atts) {
		listSize = atts.size()
		fileNameStub = atts[4] + "-" + atts[5]
	
		if (picType == "big") {
			fileName = "${fileNameStub}-large.jpg"
			fileName = "${Globals.bigPicsDir}/${fileName}"
		} else {
			fileName = "${fileNameStub}-thumb.jpg"
			fileName = "${Globals.thumbsDir}/${fileName}"
		}
	} else {
	    throw new Exception("getPicFileName:  WTF?  Got no matches for '/' in string :${src}")
	}
	return fileName
}

List<Image> getPics(HtmlPage page, List<Image> images){

	def photoPageLink = page.getByXPath("//a[@class='whiteLink' and @title='View All' and @data-action='open-photo-tab']")[0]
	HtmlPage photoPage = photoPageLink.click()
	def thumbs = getThumbs(photoPage, images)
	return thumbs
}

// Data cleansing routines
def convertIt(String value, String type) {  //Most common data cleansing tasks
	try {
		switch (type) {
			case "String":
				return value
			break
			case "Double":
				def valueAsDouble = new Double("0")
				if (! (value = cleanNotAvailable(value))){ return valueAsDouble }
				if (! (value = cleanMoney(value))){ return valueAsDouble }
				if (! checkNumber(value)) { return valueAsDouble }
				try {
					return new Double(value)
				} catch (NumberFormatException e) {
					showException("convertIt:Double:BadNumberFormat", e, 'n', null)
				} 
			break
			case "BigDecimal":
				def valueAsBigDecimal = new BigDecimal("0")
				if (! (value = cleanNotAvailable(value))){ return valueAsBigDecimal }
				if (! (value = cleanMoney(value))){ return valueAsBigDecimal }
				if (! checkNumber(value)) { return valueAsBigDecimal }
				try {
					return new BigDecimal(value).setScale(2, BigDecimal.ROUND_HALF_UP)
				} catch (NumberFormatException e) {
					showException("convertIt:BigDecimal:BadNumberFormat", e, 'n', null)
				}
			break
			case "Integer":
				def valueAsInteger = new Integer("0")
				if (! (value = cleanNotAvailable(value))){ return valueAsInteger }
				if (! (value = cleanMoney(value))){ return valueAsInteger }
				if (! checkNumber(value)) { return valueAsInteger }
				try {
					return new Integer(value)
				} catch (NumberFormatException e) {
					showException("convertIt:Integer:BadNumberFormat", e, 'n', null)
				} 
			break
			case "int":
				def valueAsInt = new Integer("0").intValue()
				if (! (value = cleanNotAvailable(value))){ return valueAsInt }
				if (! (value = cleanMoney(value))){ return valueAsInt }
				if (! checkNumber(value)) { return valueAsInt }
				
				try {
					return new Integer(value).intValue()
				} catch (NumberFormatException e) {
					showException("convertIt:int:BadNumberFormat", e, 'n', null)
				}
			break
			case "Date":
			if (value ==~ /^\d\d\d\d$/) {
				try {
					SimpleDateFormat format = new SimpleDateFormat("yyyy")
					return format.parse(value)
				} catch (IllegalArgumentException illArgEx) {
					showException("convertIt:Date:BadDateFormat", illArgEx, 'n', null)
				} catch (NullPointerException nullEx) {
					showException("convertIt:Date:BadDateFormat", nullEx, 'n', null)
				}
			}
			
			break
			case "SqlDate":
				if (value ==~ /^\d\d\d\d$/) {
					try {
						SimpleDateFormat format = new SimpleDateFormat("yyyy")
						//java.sql.Date myDate = format.parse(value)
					
						java.util.Date myDate = format.parse(value)
						java.sql.Date mySqlDate = new java.sql.Date(myDate.getTime())
						return mySqlDate
					} catch (IllegalArgumentException illArgEx) {
						showException("convertIt:Date:BadDateFormat", illArgEx, 'n', null)
					} catch (NullPointerException nullEx) {
						showException("convertIt:Date:BadDateFormat", nullEx, 'n', null)
					}
				}
				
			break
			case "Timestamp":
			if (value ==~ /^\d\d\d\d$/) {
				try {
					SimpleDateFormat format = new SimpleDateFormat("yyyy")
					//java.sql.Date myDate = format.parse(value)
				
					java.util.Date myDate = format.parse(value)
					java.sql.Timestamp myTimestamp = new java.sql.Timestamp(myDate.getTime())
					return myTimestamp
				} catch (IllegalArgumentException illArgEx) {
					showException("convertIt:Date:BadDateFormat", illArgEx, 'n', null)
				} catch (NullPointerException nullEx) {
					showException("convertIt:Date:BadDateFormat", nullEx, 'n', null)
				}
			}
			break
			default: 
				return "N/A"
			break
		}
	} catch (Exception ex) {
		showException("convertIt", ex, "n", "no")
		return "N/A"
	}
}

def getParser(HtmlPage page){
	@Grab(group='org.ccil.cowan.tagsoup', module='tagsoup', version='1.2')
	def tagsoupParser = new org.ccil.cowan.tagsoup.Parser()
	def slurper = new XmlSlurper(tagsoupParser)
	def myParser = slurper.parseText(page.asXml())
	return myParser
}

def getParser(String snippet){
	@Grab(group='org.ccil.cowan.tagsoup', module='tagsoup', version='1.2')
	def tagsoupParser = new org.ccil.cowan.tagsoup.Parser()
	def slurper = new XmlSlurper(tagsoupParser)
	def myParser = slurper.parseText(snippet)
	return myParser
}

// Utilities: TODO: Move into a library
def showException(method, exception, exit, log) {
	println("Error in $method" + "(): " + exception)
	if (exit == "y") { 
		println("Exiting...") 
		System.exit(0) 
	}
	// TODO: Add code to log to logfile if ${log} is set to "yes"
	return
}

def cleanNotAvailable(value){
	value = (value =~ /N\/A/).replaceAll("")
	return value
}

def cleanMoney(money) {
	money = (money =~ /\$/).replaceAll("")
	money = (money =~ /,/).replaceAll("")
	return money
}

def checkNumber(value){
	if (value ==~ /^[+-]?\d+\.?\d*$/) {
		return true
	} else {
		return false
	}
}

def splitIt(text, splitchar){
	def result = []
	def word = ''
	text.each{ ch ->
		if (ch == splitchar) {
			if (word) result += word
			word = ''
		} else word += ch
	}
	if (word) result += word
	return result
}

  /**
   * This method ensures that the output String has only
   * valid XML unicode characters as specified by the
   * XML 1.0 standard. For reference, please see
   * <a href="http://www.w3.org/TR/2000/REC-xml-20001006#NT-Char">the
   * standard</a>. This method will return an empty
   * String if the input is null or empty.
   *
   * @param in The String whose non-valid characters we want to remove.
   * @return The in String, stripped of non-valid characters.
   */
  def stripNonValidXMLCharacters(lines) {
      StringBuffer out = new StringBuffer() // Used to hold the output.
      char current; // Used to reference the current character.

      if (lines == null || ("".equals(lines))) return "" // vacancy test.
      for (int i = 0; i < lines.length(); i++) {
          current = lines.charAt(i) // NOTE: No IndexOutOfBoundsException caught here; it should not happen.
          if ((current == 0x9) ||
              (current == 0xA) ||
              (current == 0xD) ||
              ((current >= 0x20) && (current <= 0xD7FF)) ||
              ((current >= 0xE000) && (current <= 0xFFFD)) ||
              ((current >= 0x10000) && (current <= 0x10FFFF)))
              out.append(current)
      }
      return out.toString()
  } 
  
  def handleAddressInsertException(reason) throws Exception {
	  
	  switch (reason) {
		  //This reason is supposed to be caused when the unique constraint on address.fullAddress is violated.  
		  // If address.fullAddress already exists, set addressId to existing and do updateAddress() in case the zip has changed.
		  case ~/(?i)Duplicate Address/:
			  totalDuplicateAddresses++
			  try {
			      def addressId = getAddressId(address)
				  if (addressId) {
					  address.addressId = addressId
					  updateAddress(address)
				  } else {
				      throw new Exception("handleAddressInsertException():  Got null address_id when there are multiple...")
				  }
			  } catch (Exception ex) {
			      throw ex
			  }
		  break
		  default:
		  	  println("Unable to handle address insert exception: |${reason}|")
			  println("Insert of address faled...")
			  println("                        Data =  " + address.toString())
		  break
	  } //End switch(reason)
  }
  
  def copyToPropertyHistory(savedProperty) {
	  def historicProperty = new PropertyHistory()
	  historicProperty = setPropertyHistory(savedProperty)
	  saveHistoricProperty(historicProperty, 'insert')
  }
  
  def saveHistoricProperty(PropertyHistory historicProperty, String action) throws SQLException {
	  switch (action) {
		  case ~/insert/:
		      try {
			      insertPropertyHistory(historicProperty)
		      } catch (SQLException sqlex) {
			      println("Got exception inserting into PropertyHistory table: " + sqlex.getMessage() + "\n\n" + "Data = " + "\n" + historicProperty.toString())
				  totalSQLExceptions++
				  throw sqlex
		      }
		      break
		  case ~/update/:
		      try {
				  updatePropertyHistory(historicProperty)
			  } catch (SQLException sqlex) {
			      println("Got exception updating PropertyHistory table: " + sqlex.getMessage() + "\n\n" + "Data = " + "\n" + historicProperty.toString())
				  totalSQLExceptions++
				  throw sqlex
		      }
		      break
		  default:
		      println("saveHistoricProperty(): Unknow action - |${action}|")
		      break
	  }
	  
  }
  
  def deleteProperty(Property aProperty) throws SQLException {
	  def stmt = 'delete from property where id = ${aProperty.id}'
	  
	  try {
	      dbConnection.execute(stmt)
	  } catch (SQLException sqlex) {
	      println("Got exception deleting from Property table: " + sqlex.getMessage() + "\n\n" + "Data = " + "\n" + aProperty.toString())
		  totalSQLExceptions++
		  throw sqlex
	  }
  }
  
  def saveProperty(Property aProperty, String action) throws SQLException {
	  switch (action) {
		  case ~/insert/:
			  try {
				  insertProperty(aProperty)
			  } catch (SQLException sqlex) {
				  println("Got exception inserting into Property table: " + sqlex.getMessage() + "\n\n" + "Data = " + "\n" + aProperty.toString())
				  totalSQLExceptions++
				  throw sqlex
			  }
			  break
		  case ~/update/:
			  try {
				  updateProperty(aProperty)
			  } catch (SQLException sqlex) {
				  println("Got exception updating Property table: " + sqlex.getMessage() + "\n\n" + "Data = " + "\n" + aProperty.toString())
				  totalSQLExceptions++
				  throw sqlex
			  }
			  break
		  default:
			  println("saveProperty(): Unknow action - |${action}|")
			  break
	  }
	  
  }
  
  def getPropertyAtAddress(address) {
	  //This should only return one row...
	  savedProperty = dbConnection.firstRow("select * from property where address_id = ${address.id}")
	  return savedProperty
  }
  
  def setPropertyHistory(groovy.sql.GroovyRowResult savedProperty) {
	  def historicProperty = new PropertyHistory()
	  historicProperty.active = savedProperty.active
	  historicProperty.addressId = savedProperty.address_id
	  historicProperty.amenities = savedProperty.amenities
	  historicProperty.appliances = savedProperty. appliances
	  historicProperty.assessedValue = savedProperty.assessed_value
	  historicProperty.attic = savedProperty.attic
	  historicProperty.avgTaxes = savedProperty.avg_taxes
	  historicProperty.basement = savedProperty.basement
	  historicProperty.basementFinishedPct = savedProperty.basement_finished_pct 
	  historicProperty.baths = savedProperty.baths
	  historicProperty.beds = savedProperty.beds
	  historicProperty.cableReady = savedProperty.cable_ready 
	  historicProperty.condoProject = savedProperty.condo_project
	  historicProperty.coolingType = savedProperty.cooling_type
	  historicProperty.counterTops = savedProperty.counter_tops
	  historicProperty.dateCreated = savedProperty.date_created
	  historicProperty.last_updated = savedProperty.last_updated
	  historicProperty.daysOnMkt = savedProperty.days_on_mkt
	  historicProperty.deckOrBalcony = savedProperty.deck_or_balcony
	  historicProperty.description = savedProperty.description
	  historicProperty.design = savedProperty.design
	  historicProperty.district = savedProperty.district
	  historicProperty.driveway = savedProperty.driveway
	  historicProperty.elementarySchool = savedProperty.elementary_school 
	  historicProperty.elevator = savedProperty.elevator
	  historicProperty.exteriorMaterial = savedProperty.exterior_material 
	  historicProperty.features = savedProperty.features 
	  historicProperty.fencing = savedProperty.fencing 
	  historicProperty.floodZone = savedProperty.flood_zone 
	  historicProperty.floorCovering = savedProperty.floor_covering 
	  historicProperty.floorNumber = savedProperty.floor_number 
	  historicProperty.foundation = savedProperty.foundation 
	  historicProperty.frontage = savedProperty.frontage 
	  historicProperty.halfbaths = savedProperty.halfbaths 
	  historicProperty.heatingCooling = savedProperty.heating_cooling 
	  historicProperty.heatingType = savedProperty.heating_type 
	  historicProperty.highSchool = savedProperty.high_school 
	  historicProperty.hoaDuesPerMonth = savedProperty.hoa_dues_per_month 
	  historicProperty.homeOwnerIns = savedProperty.home_owner_ins 
	  historicProperty.hotTubSpa = savedProperty.hot_tub_spa 
	  historicProperty.houseSize = savedProperty.house_size 
	  historicProperty.houseSizeUnits = savedProperty.house_size_units 
	  historicProperty.insPeriod = savedProperty.ins_period 
	  historicProperty.internetSvc = savedProperty.internet_svc 
	  historicProperty.kitchenType = savedProperty.kitchen_type 
	  historicProperty.lastSoldDate = savedProperty.last_sold_date 
	  historicProperty.lastSoldPrice = savedProperty.last_sold_price 
	  historicProperty.lotDesc = savedProperty.lot_desc 
	  historicProperty.lotSize = savedProperty.lot_size 
	  historicProperty.lotSizeUnits = savedProperty.lot_size_units 
	  historicProperty.maintenanceFee = savedProperty.maintenance_fee
	  historicProperty.middleSchool = savedProperty.middle_school 
	  historicProperty.mls = savedProperty.mls 
	  historicProperty.mortRate = savedProperty.mort_rate 
	  historicProperty.mortTerm = savedProperty.mort_term 
	  historicProperty.numFireplaces = savedProperty.num_fireplaces 
	  historicProperty.numStories = savedProperty.num_stories 
	  historicProperty.originalListPrice = savedProperty.original_list_price 
	  historicProperty.otherFeatures = savedProperty.other_features 
	  historicProperty.parkingSpaces = savedProperty.parking_spaces 
	  historicProperty.parkingType = savedProperty.parking_type 
	  historicProperty.phoneSvc = savedProperty.phone_svc 
	  historicProperty.pool = savedProperty.pool
	  historicProperty.power = savedProperty.power 
	  historicProperty.preForeclosure = savedProperty.pre_foreclosure 
	  historicProperty.price = savedProperty.price 
	  historicProperty.priceChange = savedProperty.price_change 
	  historicProperty.pricePerSqFt = savedProperty.price_per_sq_ft 
	  historicProperty.propertyId = savedProperty.id 						// Important
	  historicProperty.propertyTax = savedProperty.property_tax 
	  historicProperty.propertyType = savedProperty.property_type 
	  historicProperty.rentEstimate = savedProperty.rent_estimate 
	  historicProperty.rentEstimateLastUpdated = savedProperty.rent_estimate_last_updated 
	  historicProperty.reo = savedProperty.reo 
	  historicProperty.road = savedProperty.road 
	  historicProperty.roofType = savedProperty.roof_type 
	  historicProperty.roomTypes = savedProperty.room_types 
	  historicProperty.sauna = savedProperty.sauna 
	  historicProperty.schoolDistrict = savedProperty.school_district 
	  historicProperty.secSystem = savedProperty.sec_system 
	  historicProperty.sewer = savedProperty.sewer 
	  historicProperty.shortSale = savedProperty.short_sale 
	  historicProperty.solidWasteDisposal = savedProperty.solid_waste_disposal 
	  historicProperty.source = savedProperty.source 
	  historicProperty.status = savedProperty.status 
	  historicProperty.subDivision = savedProperty.sub_division 
	  historicProperty.taxKey = savedProperty.tax_key 
	  historicProperty.tenure = savedProperty.tenure 
	  historicProperty.topography = savedProperty.topography 
	  historicProperty.trashSvc = savedProperty.trash_svc 
	  historicProperty.tvSvc = savedProperty.tv_svc 
	  historicProperty.lastUpdated = savedProperty.last_updated
	  historicProperty.utilities = savedProperty.utilities 
	  historicProperty.view = savedProperty.view 
	  historicProperty.walkScore = savedProperty.walk_score 
	  historicProperty.water = savedProperty.water 
	  historicProperty.windowCoverings = savedProperty.window_coverings 
	  historicProperty.yearBuilt = savedProperty.year_built 
	  historicProperty.zestimate = savedProperty.zestimate 
	  historicProperty.zestimateLastUpdated = savedProperty.zestimate_last_updated 
	  historicProperty.zoning = savedProperty.zoning 
	  
	  return historicProperty
  }
  
  def insertProperty(Property aProperty) {
	  int nextval = 0
	  //nextval = getNextId()
	  
      def stmt = """
	      insert into property (
		  version,
		  active,
		  address_id,
		  amenities,
		  appliances,
		  assessed_value,
		  attic,
		  avg_taxes,
		  basement,
		  basement_finished_pct,
		  baths,
		  beds,
		  cable_ready,
		  condo_project,
		  cooling_type,
		  counter_tops,
		  date_created,
		  days_on_mkt,
		  deck_or_balcony,
		  description,
		  design,
		  district,
		  driveway,
		  elementary_school,
		  elevator,
		  exterior_material,
		  features,
		  fencing,
		  flood_zone,
		  floor_covering,
		  floor_number,
		  foundation,
		  frontage,
		  halfbaths,
		  heating_cooling,
		  heating_type,
		  high_school,
		  hoa_dues_per_month,
		  home_owner_ins,
		  hot_tub_spa,
		  house_size,
		  house_size_units,
		  ins_period,
		  internet_svc,
		  kitchen_type,
		  last_sold_date,
		  last_sold_price,
		  lot_desc,
		  lot_size,
		  lot_size_units,
		  maintenance_fee,
		  middle_school,
		  mls,
		  mort_rate,
		  mort_term,
		  num_fireplaces,
		  num_stories,
		  original_list_price,
		  other_features,
		  parking_spaces,
		  parking_type,
		  phone_svc,
		  pool,
		  power,
		  pre_foreclosure,
		  price,
		  price_change,
		  price_per_sq_ft,
		  property_tax,
		  property_type,
		  rent_estimate,
		  rent_estimate_last_updated,
		  reo,
		  road,
		  roof_type,
		  room_types,
		  sauna,
		  school_district,
		  sec_system,
		  sewer,
		  short_sale,
		  solid_waste_disposal,
		  source,
		  status,
		  sub_division,
		  tax_key,
		  tenure,
		  topography,
		  trash_svc,
		  tv_svc,
		  utilities,
		  view,
		  walk_score,
		  water,
		  window_coverings,
		  year_built,
		  zestimate,
		  zestimate_last_updated,
		  zoning
	  ) values (
		  $aProperty.version,
		  $aProperty.active,
		  $aProperty.addressId,
		  $aProperty.amenities,
		  $aProperty.appliances,
		  $aProperty.assessedValue,
		  $aProperty.attic,
		  $aProperty.avgTaxes,
		  $aProperty.basement,
		  $aProperty.basementFinishedPct,
		  $aProperty.baths,
		  $aProperty.beds,
		  $aProperty.cableReady,
		  $aProperty.condoProject,
		  $aProperty.coolingType,
		  $aProperty.counterTops,
		  $now,
		  $aProperty.daysOnMkt,
		  $aProperty.deckOrBalcony,
		  $aProperty.description,
		  $aProperty.design,
		  $aProperty.district,
		  $aProperty.driveway,
		  $aProperty.elementarySchool,
		  $aProperty.elevator,
		  $aProperty.exteriorMaterial,
		  $aProperty.features,
		  $aProperty.fencing,
		  $aProperty.floodZone,
		  $aProperty.floorCovering,
		  $aProperty.floorNumber,
		  $aProperty.foundation,
		  $aProperty.frontage,
		  $aProperty.halfbaths,
		  $aProperty.heatingCooling,
		  $aProperty.heatingType,
		  $aProperty.highSchool,
		  $aProperty.hoaDuesPerMonth,
		  $aProperty.homeOwnerIns,
		  $aProperty.hotTubSpa,
		  $aProperty.houseSize,
		  $aProperty.houseSizeUnits,
		  $aProperty.insPeriod,
		  $aProperty.internetSvc,
		  $aProperty.kitchenType,
		  $aProperty.lastSoldDate,
		  $aProperty.lastSoldPrice,
		  $aProperty.lotDesc,
		  $aProperty.lotSize,
		  $aProperty.lotSizeUnits,
		  $aProperty.maintenanceFee,
		  $aProperty.middleSchool,
		  $aProperty.mls,
		  $aProperty.mortRate,
		  $aProperty.mortTerm,
		  $aProperty.numFireplaces,
		  $aProperty.numStories,
		  $aProperty.originalListPrice,
		  $aProperty.otherFeatures,
		  $aProperty.parkingSpaces,
		  $aProperty.parkingType,
		  $aProperty.phoneSvc,
		  $aProperty.pool,
		  $aProperty.power,
		  $aProperty.preForeclosure,
		  $aProperty.price,
		  $aProperty.priceChange,
		  $aProperty.pricePerSqFt,
		  $aProperty.propertyTax,
		  $aProperty.propertyType,
		  $aProperty.rentEstimate,
		  $aProperty.rentEstimateLastUpdated,
		  $aProperty.reo,
		  $aProperty.road,
		  $aProperty.roofType,
		  $aProperty.roomTypes,
		  $aProperty.sauna,
		  $aProperty.schoolDistrict,
		  $aProperty.secSystem,
		  $aProperty.sewer,
		  $aProperty.shortSale,
		  $aProperty.solidWasteDisposal,
		  $aProperty.source,
		  $aProperty.status,
		  $aProperty.subDivision,
		  $aProperty.taxKey,
		  $aProperty.tenure,
		  $aProperty.topography,
		  $aProperty.trashSvc,
		  $aProperty.tvSvc,
		  $aProperty.utilities,
		  $aProperty.view,
		  $aProperty.walkScore,
		  $aProperty.water,
		  $aProperty.windowCoverings,
		  $aProperty.yearBuilt,
		  $aProperty.zestimate,
		  $aProperty.zestimateLastUpdated,
		  $aProperty.zoning
	  )
      """
	  dbConnection.execute(stmt)
  }
  
  def insertPropertyHistory(PropertyHistory propertyHistory) {
	  int nextval = 0
	  //nextval = getNextId()
	  
	  def stmt = """
	      insert into property_history (
		  version,
		  active,
		  address_id,
		  amenities,
		  appliances,
		  assessed_value,
		  attic,
		  avg_taxes,
		  basement,
		  basement_finished_pct,
		  baths,
		  beds,
		  cable_ready,
		  condo_project,
		  cooling_type,
		  counter_tops,
		  date_created,
		  days_on_mkt,
		  deck_or_balcony,
		  description,
		  design,
		  district,
		  driveway,
		  elementary_school,
		  elevator,
		  exterior_material,
		  features,
		  fencing,
		  flood_zone,
		  floor_covering,
		  floor_number,
		  foundation,
		  frontage,
		  halfbaths,
		  heating_cooling,
		  heating_type,
		  high_school,
		  hoa_dues_per_month,
		  home_owner_ins,
		  hot_tub_spa,
		  house_size,
		  house_size_units,
		  ins_period,
		  internet_svc,
		  kitchen_type,
		  last_sold_date,
		  last_sold_price,
		  lot_desc,
		  lot_size,
		  lot_size_units,
		  maintenance_fee,
		  middle_school,
		  mls,
		  mort_rate,
		  mort_term,
		  num_fireplaces,
		  num_stories,
		  original_list_price,
		  other_features,
		  parking_spaces,
		  parking_type,
		  phone_svc,
		  pool,
		  power,
		  pre_foreclosure,
		  price,
		  price_change,
		  price_per_sq_ft,
		  property_id,
		  property_tax,
		  property_type,
		  rent_estimate,
		  rent_estimate_last_updated,
		  reo,
		  road,
		  roof_type,
		  room_types,
		  sauna,
		  school_district,
		  sec_system,
		  sewer,
		  short_sale,
		  solid_waste_disposal,
		  source,
		  status,
		  sub_division,
		  tax_key,
		  tenure,
		  topography,
		  trash_svc,
		  tv_svc,
		  utilities,
		  view,
		  walk_score,
		  water,
		  window_coverings,
		  year_built,
		  zestimate,
		  zestimate_last_updated,
		  zoning
	  ) values (
		  ${propertyHistory.version},
		  ${propertyHistory.active},
		  ${propertyHistory.addressId},
		  ${propertyHistory.amenities},
		  ${propertyHistory.appliances},
		  ${propertyHistory.assessedValue},
		  ${propertyHistory.attic},
		  ${propertyHistory.avgTaxes},
		  ${propertyHistory.basement},
		  ${propertyHistory.basementFinishedPct},
		  ${propertyHistory.baths},
		  ${propertyHistory.beds},
		  ${propertyHistory.cableReady},
		  ${propertyHistory.condoProject},
		  ${propertyHistory.coolingType},
		  ${propertyHistory.counterTops},
		  ${now},
		  ${propertyHistory.daysOnMkt},
		  ${propertyHistory.deckOrBalcony},
		  ${propertyHistory.description},
		  ${propertyHistory.design},
		  ${propertyHistory.district},
		  ${propertyHistory.driveway},
		  ${propertyHistory.elementarySchool},
		  ${propertyHistory.elevator},
		  ${propertyHistory.exteriorMaterial},
		  ${propertyHistory.features},
		  ${propertyHistory.fencing},
		  ${propertyHistory.floodZone},
		  ${propertyHistory.floorCovering},
		  ${propertyHistory.floorNumber},
		  ${propertyHistory.foundation},
		  ${propertyHistory.frontage},
		  ${propertyHistory.halfbaths},
		  ${propertyHistory.heatingCooling},
		  ${propertyHistory.heatingType},
		  ${propertyHistory.highSchool},
		  ${propertyHistory.hoaDuesPerMonth},
		  ${propertyHistory.homeOwnerIns},
		  ${propertyHistory.hotTubSpa},
		  ${propertyHistory.houseSize},
		  ${propertyHistory.houseSizeUnits},
		  ${propertyHistory.insPeriod},
		  ${propertyHistory.internetSvc},
		  ${propertyHistory.kitchenType},
		  ${propertyHistory.lastSoldDate},
		  ${propertyHistory.lastSoldPrice},
		  ${propertyHistory.lotDesc},
		  ${propertyHistory.lotSize},
		  ${propertyHistory.lotSizeUnits},
		  ${propertyHistory.maintenanceFee},
		  ${propertyHistory.middleSchool},
		  ${propertyHistory.mls},
		  ${propertyHistory.mortRate},
		  ${propertyHistory.mortTerm},
		  ${propertyHistory.numFireplaces},
		  ${propertyHistory.numStories},
		  ${propertyHistory.originalListPrice},
		  ${propertyHistory.otherFeatures},
		  ${propertyHistory.parkingSpaces},
		  ${propertyHistory.parkingType},
		  ${propertyHistory.phoneSvc},
		  ${propertyHistory.pool},
		  ${propertyHistory.power},
		  ${propertyHistory.preForeclosure},
		  ${propertyHistory.price},
		  ${propertyHistory.priceChange},
		  ${propertyHistory.pricePerSqFt},
		  ${propertyHistory.propertyId},
		  ${propertyHistory.propertyTax},
		  ${propertyHistory.propertyType},
		  ${propertyHistory.rentEstimate},
		  ${propertyHistory.rentEstimateLastUpdated},
		  ${propertyHistory.reo},
		  ${propertyHistory.road},
		  ${propertyHistory.roofType},
		  ${propertyHistory.roomTypes},
		  ${propertyHistory.sauna},
		  ${propertyHistory.schoolDistrict},
		  ${propertyHistory.secSystem},
		  ${propertyHistory.sewer},
		  ${propertyHistory.shortSale},
		  ${propertyHistory.solidWasteDisposal},
		  ${propertyHistory.source},
		  ${propertyHistory.status},
		  ${propertyHistory.subDivision},
		  ${propertyHistory.taxKey},
		  ${propertyHistory.tenure},
		  ${propertyHistory.topography},
		  ${propertyHistory.trashSvc},
		  ${propertyHistory.tvSvc},
		  ${propertyHistory.utilities},
		  ${propertyHistory.view},
		  ${propertyHistory.walkScore},
		  ${propertyHistory.water},
		  ${propertyHistory.windowCoverings},
		  ${propertyHistory.yearBuilt},
		  ${propertyHistory.zestimate},
		  ${propertyHistory.zestimateLastUpdated},
		  ${propertyHistory.zoning}
	  )
      """
	  dbConnection.execute(stmt)
  }
  
  def updateProperty(Property aProperty) {
	  def stmt = ""

	  stmt = """
	      update property set
			  active = ${aProperty.active}
			  address_id = ${aProperty.addressId}
			  amenities = ${aProperty.amenities}
			  appliances = ${aProperty. appliances}
			  assessed_value = ${aProperty.assessedValue}
			  attic = ${aProperty.attic}
			  avg_taxes = ${aProperty.avgTaxes}
			  basement = ${aProperty.basement}
			  basement_finished_pct = ${aProperty.basementFinishedPct} 
			  baths = ${aProperty.baths}
			  beds = ${aProperty.beds}
			  cable_ready = ${aProperty.cableReady} 
			  condo_project = ${aProperty.condoProject}
			  cooling_type = ${aProperty.coolingType}
			  counter_tops = ${aProperty.counterTops}
			  days_on_mkt = ${aProperty.daysOnMkt}
			  deck_or_balcony = ${aProperty.deckOrBalcony}
			  description = ${aProperty.description}
			  design = ${aProperty.design}
			  district = ${aProperty.district}
			  driveway = ${aProperty.driveway}
			  elementary_school = ${aProperty.elementarySchool} 
			  elevator = ${aProperty.elevator}
			  exterior_material = ${aProperty.exteriorMaterial} 
			  features = ${aProperty.features} 
			  fencing = ${aProperty.fencing} 
			  flood_zone = ${aProperty.floodZone}
			  floor_covering = ${aProperty.floorCovering} 
			  floor_number = ${aProperty.floorNumber} 
			  foundation = ${aProperty.foundation} 
			  frontage = ${aProperty.frontage} 
			  halfbaths = ${aProperty.halfbaths} 
			  heating_cooling = ${aProperty.heatingCooling} 
			  heating_type = ${aProperty.heatingType}
			  high_school = ${aProperty.highSchool} 
			  hoa_dues_per_month = ${aProperty.hoaDuesPerMonth} 
			  home_owner_ins = ${aProperty.homeOwnerIns} 
			  hot_tub_spa = ${aProperty.hotTubSpa} 
			  house_size = ${aProperty.houseSize} 
			  house_size_units = ${aProperty.houseSizeUnits} 
			  ins_period = ${aProperty.insPeriod} 
			  internet_svc = ${aProperty.internetSvc} 
			  kitchen_type = ${aProperty.kitchenType} 
			  last_sold_date = ${aProperty.lastSoldDate} 
			  last_sold_price = ${aProperty.lastSoldPrice} 
			  last_updated = ${aProperty.lastUpdated}
			  lot_desc = ${aProperty.lotDesc} 
			  lot_size = ${aProperty.lotSize} 
			  lot_size_units = ${aProperty.lotSizeUnits} 
			  maintenance_fee = ${aProperty.maintenanceFee}
			  middle_school = ${aProperty.middleSchool} 
			  mls = ${aProperty.mls} 
			  mort_rate = ${aProperty.mortRate} 
			  mort_term = ${aProperty.mortTerm} 
			  num_fireplaces = ${aProperty.numFireplaces} 
			  num_stories = ${aProperty.numStories} 
			  original_list_price = ${aProperty.originalListPrice} 
			  other_features = ${aProperty.otherFeatures} 
			  parking_spaces = ${aProperty.parkingSpaces} 
			  parking_type = ${aProperty.parkingType} 
			  phone_svc = ${aProperty.phoneSvc} 
			  pool = ${aProperty.pool}
			  power = ${aProperty.power} 
			  pre_foreclosure = ${aProperty.preForeclosure} 
			  price = ${aProperty.price}
			  price_change = ${aProperty.priceChange} 
			  price_per_sq_ft = ${aProperty.pricePerSqFt} 				
			  property_tax = ${aProperty.propertyTax} 
			  property_type = ${aProperty.propertyType} 
			  rent_estimate = ${aProperty.rentEstimate} 
			  rent_estimate_last_updated = ${aProperty.rentEstimateLastUpdated} 
			  reo = ${aProperty.reo} 
			  road = ${aProperty.road} 
			  roof_type = ${aProperty.roofType} 
			  room_types = ${aProperty.roomTypes} 
			  sauna = ${aProperty.sauna} 
			  school_district = ${aProperty.schoolDistrict} 
			  sec_system = ${aProperty.secSystem}
			  sewer = ${aProperty.sewer} 
			  short_sale = ${aProperty.shortSale} 
			  solid_waste_disposal = ${aProperty.solidWasteDisposal} 
			  source = ${aProperty.source} 
			  status = ${aProperty.status} 
			  sub_division = ${aProperty.subDivision} 
			  tax_key = ${aProperty.taxKey} 
			  tenure = ${aProperty.tenure} 
			  topography = ${aProperty.topography} 
			  trash_svc = ${aProperty.trashSvc} 
			  tv_svc = ${aProperty.tvSvc} 
			  last_updated = ${now} 
			  utilities = ${aProperty.utilities} 
			  view = ${aProperty.view} 
			  walk_score = ${aProperty.walkScore} 
			  water = ${aProperty.water} 
			  window_coverings = ${aProperty.windowCoverings} 
			  year_built = ${aProperty.yearBuilt} 
			  zestimate = ${aProperty.zestimate} 
			  zestimate_last_updated = ${aProperty.zestimateLastUpdated} 
			  zoning = ${aProperty.zoning} 
	  	  where address_id = ${aProperty.id}
      """
	  dbConnection.execute(stmt)
  }
  
  def updatePropertyHistory(PropertyHistory aProperty) {
	  def stmt = """
	      update property_history set
			  active = ${aProperty.active}
			  address_id = ${aProperty.addressId}
			  amenities = ${aProperty.amenities}
			  appliances = ${aProperty. appliances}
			  assessed_value = ${aProperty.assessedValue}
			  attic = ${aProperty.attic}
			  avg_taxes = ${aProperty.avgTaxes}
			  basement = ${aProperty.basement}
			  basement_finished_pct = ${aProperty.basementFinishedPct} 
			  baths = ${aProperty.baths}
			  beds = ${aProperty.beds}
			  cable_ready = ${aProperty.cableReady} 
			  condo_project = ${aProperty.condoProject}
			  cooling_type = ${aProperty.coolingType}
			  counter_tops = ${aProperty.counterTops}
			  days_on_mkt = ${aProperty.daysOnMkt}
			  deck_or_balcony = ${aProperty.deckOrBalcony}
			  description = ${aProperty.description}
			  design = ${aProperty.design}
			  district = ${aProperty.district}
			  driveway = ${aProperty.driveway}
			  elementary_school = ${aProperty.elementarySchool} 
			  elevator = ${aProperty.elevator}
			  exterior_material = ${aProperty.exteriorMaterial} 
			  features = ${aProperty.features} 
			  fencing = ${aProperty.fencing} 
			  flood_zone = ${aProperty.floodZone}
			  floor_covering = ${aProperty.floorCovering} 
			  floor_number = ${aProperty.floorNumber} 
			  foundation = ${aProperty.foundation} 
			  frontage = ${aProperty.frontage} 
			  halfbaths = ${aProperty.halfbaths} 
			  heating_cooling = ${aProperty.heatingCooling} 
			  heating_type = ${aProperty.heatingType}
			  high_school = ${aProperty.highSchool} 
			  hoa_dues_per_month = ${aProperty.hoaDuesPerMonth} 
			  home_owner_ins = ${aProperty.homeOwnerIns} 
			  hot_tub_spa = ${aProperty.hotTubSpa} 
			  house_size = ${aProperty.houseSize} 
			  house_size_units = ${aProperty.houseSizeUnits} 
			  ins_period = ${aProperty.insPeriod} 
			  internet_svc = ${aProperty.internetSvc} 
			  kitchen_type = ${aProperty.kitchenType} 
			  last_sold_date = ${aProperty.lastSoldDate} 
			  last_sold_price = ${aProperty.lastSoldPrice} 
			  last_updated = ${aProperty.lastUpdated}
			  lot_desc = ${aProperty.lotDesc} 
			  lot_size = ${aProperty.lotSize} 
			  lot_size_units = ${aProperty.lotSizeUnits} 
			  maintenance_fee = ${aProperty.maintenanceFee}
			  middle_school = ${aProperty.middleSchool} 
			  mls = ${aProperty.mls} 
			  mort_rate = ${aProperty.mortRate} 
			  mort_term = ${aProperty.mortTerm} 
			  num_fireplaces = ${aProperty.numFireplaces} 
			  num_stories = ${aProperty.numStories} 
			  original_list_price = ${aProperty.originalListPrice} 
			  other_features = ${aProperty.otherFeatures} 
			  parking_spaces = ${aProperty.parkingSpaces} 
			  parking_type = ${aProperty.parkingType} 
			  phone_svc = ${aProperty.phoneSvc} 
			  pool = ${aProperty.pool}
			  power = ${aProperty.power} 
			  pre_foreclosure = ${aProperty.preForeclosure} 
			  price = ${aProperty.price}
			  price_change = ${aProperty.priceChange} 
			  price_per_sq_ft = ${aProperty.pricePerSqFt} 	
			  property_id = ${aProperty.propertyId}
			  property_tax = ${aProperty.propertyTax} 
			  property_type = ${aProperty.propertyType} 
			  rent_estimate = ${aProperty.rentEstimate} 
			  rent_estimate_last_updated = ${aProperty.rentEstimateLastUpdated} 
			  reo = ${aProperty.reo} 
			  road = ${aProperty.road} 
			  roof_type = ${aProperty.roofType} 
			  room_types = ${aProperty.roomTypes} 
			  sauna = ${aProperty.sauna} 
			  school_district = ${aProperty.schoolDistrict} 
			  sec_system = ${aProperty.secSystem}
			  sewer = ${aProperty.sewer} 
			  short_sale = ${aProperty.shortSale} 
			  solid_waste_disposal = ${aProperty.solidWasteDisposal} 
			  source = ${aProperty.source} 
			  status = ${aProperty.status} 
			  sub_division = ${aProperty.subDivision} 
			  tax_key = ${aProperty.taxKey} 
			  tenure = ${aProperty.tenure} 
			  topography = ${aProperty.topography} 
			  trash_svc = ${aProperty.trashSvc} 
			  tv_svc = ${aProperty.tvSvc} 
			  last_updated = ${now} 
			  utilities = ${aProperty.utilities} 
			  view = ${aProperty.view} 
			  walk_score = ${aProperty.walkScore} 
			  water = ${aProperty.water} 
			  window_coverings = ${aProperty.windowCoverings} 
			  year_built = ${aProperty.yearBuilt} 
			  zestimate = ${aProperty.zestimate} 
			  zestimate_last_updated = ${aProperty.zestimateLastUpdated} 
			  zoning = ${aProperty.zoning} 
	  	  where id = ${aProperty.id}
      """
	  dbConnection.execute(stmt)
  }
  
  def getSleepPeriod(int floor, int ceiling) {
	  Random rand = new Random()
	  
	  int randNum = rand.nextInt(ceiling)
	  if (randNum < floor) { randNum = floor }
	  return randNum
  }
  
    //then we intercept System.exit() to throw this exception
	  
	/**
	 * Make System.exit throw ProgramExitException to fake exiting the VM
	 */
	System.metaClass.static.invokeMethod = { String name, args ->
        if (name == 'exit') throw new ProgramExitException(args[0])
		def validMethod =  System.metaClass.getStaticMetaMethod(name, args)
		if (validMethod != null) {
			validMethod.invoke(delegate, args)
		}
		else {
			return  System.metaClass.invokeMissingMethod(delegate, name, args)
		}
	}
	  
	//and lastly we have GroovyShell catch any ProgramExitException and return the status code from the run method.
	  
	/**
	 * Catch ProgramExitException exceptions to mimic exit status codes
	 * without exiting the VM
	 */
	GroovyShell.metaClass.invokeMethod = { String name, args ->
		def validMethod = GroovyShell.metaClass.getMetaMethod(name, args)
		if (validMethod != null) {
			try {
				validMethod.invoke(delegate, args)
			} catch (ProgramExitException e) {
				return e.statusCode
			}
		}
		else {
			return GroovyShell.metaClass.invokeMissingMethod(delegate, name, args)
		}
	 }

	 /********************** End of download script ******************************************************/
	
    class ProgramExitException extends RuntimeException {
		
			int statusCode
		
			public ProgramExitException(int statusCode) {
				super("Exited with " + statusCode)
				this.statusCode = statusCode
			}
	}
	
	public class FileUtils {
		/**
		 * By default File#delete fails for non-empty directories, it works like "rm".
		 * We need something a little more brutual - this does the equivalent of "rm -r"
		 * @param path Root File Path
		 * @return true iff the file and all sub files/directories have been removed
		 * @throws FileNotFoundException
		 */
		public static boolean deleteRecursive(File path) throws FileNotFoundException{
			if (!path.exists()) return true;
			boolean ret = true;
			if (path.isDirectory()){
				for (File f : path.listFiles()){
					ret = ret && FileUtils.deleteRecursive(f);
				}
			}
			return ret && path.delete();
		}
	}
	
	/**
	 * User defined Exceptions
	 */
	class ImageSaveException extends java.lang.Exception {
		public ImageSaveException() { super() }
		public ImageSaveException(String message) { super(message) }
		public ImageSaveException(String message, Throwable cause) { super(message, cause) }
		public ImageSaveException(Throwable cause) { super(cause) }
	}
	
	class Globals {
		static String picsDir = "C:/temp/pics"
		static String thumbsDir = "${picsDir}/thumbs"
		static String bigPicsDir = "${picsDir}/bigPics" 
	}
  