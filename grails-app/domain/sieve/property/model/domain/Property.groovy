package sieve.property.model.domain

import java.sql.Date;


class Property {
	
	//static hasMany = [images: Image]
	
	static constraints = {  
		address nullable: false
		mls nullable: true
		propertyTax nullable: true 
		assessedValue nullable: true 
		daysOnMkt nullable: true
		district nullable: true 
		features nullable: true 
		internetSvc nullable: true
		pricePerSqFt nullable: true 
		description nullable: true 
		active nullable: false 
 		status nullable: true 
 		taxKey nullable: true 
		phoneSvc nullable: true
		tenure nullable: true 
		tvSvc nullable: true
        propertyType nullable: false 
        beds nullable: true 
        baths nullable: true 
        halfbaths nullable: true 
        houseSize nullable: false 
        houseSizeUnits nullable: true 
        parkingSpaces nullable: true 
        parkingType nullable: true 
        power nullable: true 
        sewer nullable: true 
        shortSale nullable: true 
        preForeclosure nullable: true 
        reo nullable: true 
        yearBuilt nullable: true 
        water nullable: true 
        condoProject nullable: true 
        zoning nullable: true 
        topography nullable: true   
        lotSize nullable: true 
        lotSizeUnits nullable: true 
        fencing nullable: true 
        frontage nullable: true 
        hoaDuesPerMonth nullable: true 
        appliances nullable: true 
        maintenanceFee nullable: true 
        design nullable: true 
        driveway nullable: true 
        floorCovering nullable: true 
        heatingType nullable: true 
        coolingType nullable: true 
        otherFeatures nullable: true 
        roofType nullable: true 
        windowCoverings nullable: true 
        kitchenType nullable: true 
        counterTops nullable: true 
        heatingCooling nullable: true 
        solidWasteDisposal nullable: true 
        lotDesc nullable: true 
        subDivision nullable: true 
        amenities nullable: true 
        basement nullable: true 
        basementFinishedPct nullable: true 
        numFireplaces nullable: true 
        attic nullable: true 
        exteriorMaterial nullable: true 
        view nullable: true         
        cableReady nullable: true 
        elevator nullable: true 
        hotTubSpa nullable: true 
        pool nullable: true 
        sauna nullable: true 
        secSystem nullable: true 
        roomTypes nullable: true 
        elementarySchool nullable: true 
        highSchool nullable: true 
        middleSchool nullable: true 
        schoolDistrict nullable: true 
        floorNumber nullable: true 
        numStories nullable: true 
        zestimate nullable: true 
        zestimateLastUpdated nullable: true 
        rentEstimate nullable: true 
        rentEstimateLastUpdated nullable: true 
        price nullable: false         
        originalListPrice nullable: true 
        priceChange nullable: true 
        homeOwnerIns nullable: true 
        insPeriod nullable: true 
        walkScore nullable: true 
        trashSvc nullable: true 
        mortRate nullable: true 
        mortTerm nullable: true 
        avgTaxes nullable: true 
        lastSoldPrice nullable: true 
        lastSoldDate nullable: true 
        road nullable: true 
        utilities nullable: true 
        deckOrBalcony nullable: true 
        foundation nullable: true 
        source nullable: true 
        floodZone nullable: true
	}
	
	Address		address		// Uni-directional one to one for address to property. 
							// Note: since no belongsTo = [address: Address] 
							// the address is not deleted if a property is 
							// deleted.  If there was a belongsTo = [address: Address]
							// that would make this bi-directional.
	String		mls = ""
	
	// Clarke Realty properties
	// Common to Land, Condo and Home types....
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
	Date		yearBuilt = new Date(0)
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
	Date		zestimateLastUpdated = new Date(0)
	Integer		rentEstimate = new Integer("0")
	Date		rentEstimateLastUpdated = new Date(0)
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
	Date		lastSoldDate = new Date(0)
	Date		dateCreated = new Date(0)
	Date		lastUpdated = new Date(0)
	int 		road = 0
	String		utilities = ""
	int 		deckOrBalcony = 0
	String 		foundation = ""
	String		source = ""
	int			floodZone = 0
	
	public String toString() {
		return """Property:
    id 							--	${id}
	dateCreated 				--	${dateCreated}
	lastUpdated 				--	${lastUpdated}
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
	createDate 					--	${dateCreated}
	updateDate 					--	${lastUpdated}
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
