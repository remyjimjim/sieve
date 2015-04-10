package sieve.property.model.dto

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

class PropertyHistory implements java.io.Serializable {
	String pattern = "MM/dd/yyyy";
	SimpleDateFormat format = new SimpleDateFormat(pattern);
	Timestamp 	zeroDate = new java.sql.Timestamp(0)
	Integer		id = new Integer("0")
	Integer		propertyId = new Integer("0")
	Integer		version = new Integer("0")
	Integer		addressId = new Integer("0")
	String		mls = ""
	
	BigDecimal	propertyTax = BigDecimal.ZERO
	BigDecimal	assessedValue = BigDecimal.ZERO
	Integer		daysOnMkt = new Integer("0")
	String		district = ""
	String		features = ""
	String		internetSvc = "" 			// Satellite, Wireless, ethernet, WiFi, etc
	BigDecimal 	pricePerSqFt = BigDecimal.ZERO
	String		description = ""
	int			active = 0
	String 		status = ""
	String 		taxKey = ""
	String		phoneSvc = ""             	// Land line, satellite, cell, etc
	String		tenure = ""
	String		tvSvc = ""					// Antenna, Satellite, etc
	String		propertyType = ""
	
	// Common to Home and Condo
	int			beds = 0
	int			baths = 0
	int      	halfbaths = 0
	int 		houseSize = 0
	String		houseSizeUnits = ""
	Integer		parkingSpaces = new Integer("0")
	String		parkingType = ""
	String		power = ""
	String		sewer = ""
	int			shortSale = 0
	int			preForeclosure = 0
	String		reo = ""
	Timestamp	yearBuilt = zeroDate
	String		water = ""
	String		condoProject = ""
	
	//Common to Home and Land
	String		zoning = ""
	String		topography = ""				// Fairly level, etc
	Double		lotSize = 0
	String		lotSizeUnits = ""
	String		fencing = ""
	
	// Common to Land and Condo
	String		frontage = "" 				// road/street, etc
	
	// Home only:
	BigDecimal	hoaDuesPerMonth = BigDecimal.ZERO

	// Condo Only:
	String		appliances = ""
	BigDecimal	maintenanceFee = BigDecimal.ZERO
	String 		design = ""
	String		driveway = ""
	String		floorCovering = ""
	String		heatingType = ""
	String		coolingType = ""
	String		otherFeatures = ""
	String		roofType = ""
	String		windowCoverings = ""
	String		kitchenType = ""
	String		counterTops = ""
	String		heatingCooling = ""
	String		solidWasteDisposal = ""
	
	// Land Only:
	String 		lotDesc = ""
	
	// ---------- End of Clarke properties --------------------------
	
	// Extra
	String 		subDivision = ""
	String		amenities = ""
	int			basement = 0
	Double		basementFinishedPct = BigDecimal.ZERO
	Integer		numFireplaces = new Integer("0")
	int			attic = 0
	String		exteriorMaterial = ""
	String		view = ""								// Ocean, Mountain, etc
	int			cableReady = 0
	int			elevator = 0
	int			hotTubSpa = 0
	int			pool = 0
	int			sauna = 0
	String		secSystem = ""
	String		roomTypes = ""
	String		elementarySchool = ""
	String		highSchool = ""
	String		middleSchool = ""
	String		schoolDistrict = ""
	Double		floorNumber = 0
	Double		numStories = 0
	Integer		zestimate = new Integer("0")
	Timestamp	zestimateLastUpdated = zeroDate
	Integer		rentEstimate = new Integer("0")
	Timestamp	rentEstimateLastUpdated = zeroDate
	BigDecimal	price = BigDecimal.ZERO								// done
	BigDecimal	originalListPrice = BigDecimal.ZERO
	BigDecimal	priceChange = BigDecimal.ZERO
	Double		homeOwnerIns = 0
	String		insPeriod = ""
	Integer		walkScore = new Integer("0")
	String 		trashSvc = ""
	BigDecimal	mortRate = BigDecimal.ZERO
	Integer		mortTerm = new Integer("0")
	Double		avgTaxes = 0
	Integer		lastSoldPrice = new Integer("0")
	Timestamp	lastSoldDate = zeroDate
	Timestamp	dateCreated
	Timestamp	lastUpdated
	int 		road = 0
	String		utilities = ""
	int 		deckOrBalcony = 0
	String 		foundation = ""
	String		source = ""
	int			floodZone = 0
	
	public String toString() {
		return """Property:
    id 							--	${id}
	mls 						--	${mls}
	district 					--  ${district}
	subDivision 				--  ${subDivision}
	beds 						--	${beds}
	baths 						--	${baths}
	yearBuilt 					--	${yearBuilt}
	parkingSpaces 				--	${parkingSpaces}
	parkingType 				--	${parkingType}
	daysOnMkt 					--	${daysOnMkt}
	heatingType 				--	${heatingType}
	coolingType 				--	${coolingType}
	basement 					--	${basement}
	basementFinishedPct 		--	${basementFinishedPct}
	numFireplaces 				--	${numFireplaces}
	floorCovering 				--	${floorCovering}
	attic 						--	${attic}
	roofType 					--	${roofType}
	exteriorMaterial 			--	${exteriorMaterial}
	view 						--	${view}
	cableReady 					--	${cableReady}
	elevator 					--	${elevator}
	hotTubSpa 					--	${hotTubSpa}
	pool 						--	${pool}
	sauna 						--	${sauna}
	secSystem 					--	${secSystem}
	roomTypes 					--	${roomTypes}
	elementarySchool 			--	${elementarySchool}
	highSchool 					--	${highSchool}
	middleSchool 				--	${middleSchool}
	schoolDistrict 				--	${schoolDistrict}
	floorNumber 				--	${floorNumber}
	numStories 					--	${numStories}
	zestimate 					--	${zestimate}
	zestimateLastUpdated 		--	${zestimateLastUpdated}
	rentEstimate 				--	${rentEstimate}
	rentEstimateLastUpdated 	--	${rentEstimateLastUpdated}
	lotSize 					--	${lotSize}
	lotSizeUnits 				--	${lotSizeUnits}
	propertyType 				--	${propertyType}
	price 						--	${price}
	propertyTax 				--	${propertyTax}
    propertyId 					--	${propertyId}
	assessedValue 				--	${assessedValue}
	homeOwnerIns 				--	${homeOwnerIns}
	insPeriod 					--	${insPeriod}
	description 				--	${description}
	walkScore 					--	${walkScore}
	water 						--	${water}
	condoProject 				--	${condoProject}
	power 						--	${power}
	sewer 						--	${sewer}
	trashSvc 					--	${trashSvc}
	mortRate 					--	${mortRate}
	mortTerm 					--	${mortTerm}
	avgTaxes 					--	${avgTaxes}
	lastSoldPrice 				--	${lastSoldPrice}
	lastSoldDate 				--	${lastSoldDate}
	dateCreated 					--	${dateCreated}
	lastUpdated 					--	${lastUpdated}
	utilities 					--	${utilities}
	tenure 						--	${tenure}
	hoaDuesPerMonth 			--	${hoaDuesPerMonth}
	lotDesc						--  ${lotDesc}
	foundation 					--	${foundation}
	design 						--	${design}
	deckOrBalcony 				--	${deckOrBalcony}
	source 						--	${source}
	zoning 						--  ${zoning}
	floodZone 					--  ${floodZone}
	topography					--  ${topography}
	frontage 					--  ${frontage}
	phoneSvc 					--  ${phoneSvc}
	tvSvc 						--  ${tvSvc}
	internetSvc 				--  ${internetSvc}
	shortSale 					--  ${shortSale}
	preForeclosure 				--  ${preForeclosure}
	reo 						--  ${reo}
	appliances 					--  ${appliances}
	driveway 					--  ${driveway}
	features 					--  ${features}
	otherFeatures 				--  ${otherFeatures}
	maintenanceFee 				--  ${maintenanceFee}
	windowCoverings 			--  ${windowCoverings}
      """
	}
}

