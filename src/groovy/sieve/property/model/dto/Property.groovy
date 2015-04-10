package sieve.property.model.dto

import java.sql.*
import java.text.SimpleDateFormat;
import groovy.sql.GroovyRowResult;

class Property implements java.io.Serializable {
	// Set this to true for debugging:
	boolean 	debug = false
	int			threshold = 4 //Number of diffs above which 2 properties are deemed to differ significantly
	String pattern = "MM/dd/yyyy";
	SimpleDateFormat format = new SimpleDateFormat(pattern);
	Timestamp	zeroDate = new java.sql.Timestamp(0)

	int 		numEmpty = 0
	int 		numOtherEmpty = 0
	
	Integer		id = new Integer("0")
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
    zeroDate                    --  ${zeroDate}
    id 							--	${id}
    version 					--	${version}
    yoyoyo						--  ohyoyoyo
    addressId 					--	${addressId}
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
	pricePerSqFt 				--  ${pricePerSqFt}
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
	
	public void setEmptiness(groovy.sql.GroovyRowResult otherProperty) {
		
		/* Sets class props numEmpty and numOtherEmpty.  Caller can use these props after calling this method.
		 */
		if ((this.version) && this.version.value == 0) numEmpty++
		if ((this.addressId) && this.addressId.value == 0) numEmpty++
		if ((this.mls) && this.mls == "") numEmpty++
		if ((this.propertyTax) && this.propertyTax.intVal == 0) numEmpty++
		if ((this.assessedValue) && this.assessedValue.intVal == 0) numEmpty++
		if ((this.daysOnMkt) && this.daysOnMkt.value == 0) numEmpty++
		if ((this.district) && this.district == "") numEmpty++
		if ((this.features) && this.features == "") numEmpty++
		if ((this.internetSvc) && this.internetSvc == "") numEmpty++
		if ((this.pricePerSqFt) && this.pricePerSqFt.intVal == 0) numEmpty++
		if ((this.description) && this.description == "") numEmpty++
		if ((this.active) && this.active.value == 0) numEmpty++
		if ((this.status) && this.status == "") numEmpty++
		if ((this.taxKey) && this.taxKey == "") numEmpty++
		if ((this.phoneSvc) && this.phoneSvc == "") numEmpty++
		if ((this.tenure) && this.tenure == "") numEmpty++
		if ((this.tvSvc) && this.tvSvc == "") numEmpty++
		if ((this.propertyType) && this.propertyType == "") numEmpty++
		if ((this.beds) && this.beds.value == 0) numEmpty++
		if ((this.baths) && this.baths.value == 0) numEmpty++
		if ((this.halfbaths) && this.halfbaths.value == 0) numEmpty++
		if ((this.houseSize) && this.houseSize.value == 0) numEmpty++
		if ((this.houseSizeUnits) && this.houseSizeUnits == "") numEmpty++
		if ((this.parkingSpaces) && this.parkingSpaces.value == 0) numEmpty++
		if ((this.parkingType) && this.parkingType == "") numEmpty++
		if ((this.power) && this.power == "") numEmpty++
		if ((this.sewer) && this.sewer == "") numEmpty++
		if ((this.shortSale) && this.shortSale == 0) numEmpty++
		if ((this.preForeclosure) && this.preForeclosure == 0) numEmpty++
		if ((this.reo) && this.reo == "") numEmpty++
		if ((this.yearBuilt) && this.yearBuilt == zeroDate) numEmpty++
		if ((this.water) && this.water == "") numEmpty++
		if ((this.condoProject) && this.condoProject == "") numEmpty++
		if ((this.zoning) && this.zoning == "") numEmpty++
		if ((this.topography) && this.topography == "") numEmpty++
		if ((this.lotSize) && this.lotSize == 0) numEmpty++
		if ((this.lotSizeUnits) && this.lotSizeUnits == "") numEmpty++
		if ((this.fencing) && this.fencing == "") numEmpty++
		if ((this.frontage) && this.frontage == "") numEmpty++
		if ((this.hoaDuesPerMonth) && this.hoaDuesPerMonth == BigDecimal.ZERO) numEmpty++
		if ((this.appliances) && this.appliances == "") numEmpty++
		if ((this.maintenanceFee) && this.maintenanceFee == BigDecimal.ZERO) numEmpty++
		if ((this.design) && this.design == "") numEmpty++
		if ((this.driveway) && this.driveway == "") numEmpty++
		if ((this.floorCovering) && this.floorCovering == "") numEmpty++
		if ((this.heatingType) && this.heatingType == "") numEmpty++
		if ((this.coolingType) && this.coolingType == "") numEmpty++
		if ((this.otherFeatures) && this.otherFeatures == "") numEmpty++
		if ((this.roofType) && this.roofType == "") numEmpty++
		if ((this.windowCoverings) && this.windowCoverings == "") numEmpty++
		if ((this.kitchenType) && this.kitchenType == "") numEmpty++
		if ((this.counterTops) && this.counterTops == "") numEmpty++
		if ((this.heatingCooling) && this.heatingCooling == "") numEmpty++
		if ((this.solidWasteDisposal) && this.solidWasteDisposal == "") numEmpty++
		if ((this.lotDesc) && this.lotDesc == "") numEmpty++
		if ((this.subDivision) && this.subDivision == "") numEmpty++
		if ((this.amenities) && this.amenities == "") numEmpty++
		if ((this.basement) && this.basement == 0) numEmpty++
		if ((this.basementFinishedPct) && this.basementFinishedPct == BigDecimal.ZERO) numEmpty++
		if ((this.numFireplaces) && this.numFireplaces.value == 0) numEmpty++
		if ((this.attic) && this.attic == 0) numEmpty++
		if ((this.exteriorMaterial) && this.exteriorMaterial == "") numEmpty++
		if ((this.view) && this.view == "") numEmpty++
		if ((this.cableReady) && this.cableReady == 0) numEmpty++
		if ((this.elevator) && this.elevator == 0) numEmpty++
		if ((this.hotTubSpa) && this.hotTubSpa == 0) numEmpty++
		if ((this.pool) && this.pool == 0) numEmpty++
		if ((this.sauna) && this.sauna == 0) numEmpty++
		if ((this.secSystem) && this.secSystem == "") numEmpty++
		if ((this.roomTypes) && this.roomTypes == "") numEmpty++
		if ((this.elementarySchool) && this.elementarySchool == "") numEmpty++
		if ((this.highSchool) && this.highSchool == "") numEmpty++
		if ((this.middleSchool) && this.middleSchool == "") numEmpty++
		if ((this.schoolDistrict) && this.schoolDistrict == "") numEmpty++
		if ((this.floorNumber) && this.floorNumber == 0) numEmpty++
		if ((this.numStories) && this.numStories == 0) numEmpty++
		if ((this.zestimate) && this.zestimate.value == 0) numEmpty++
		if ((this.zestimateLastUpdated) && this.zestimateLastUpdated == zeroDate) numEmpty++
		if ((this.price) && this.price == BigDecimal.ZERO) numEmpty++
		if ((this.originalListPrice) && this.originalListPrice == BigDecimal.ZERO) numEmpty++
		if ((this.priceChange) && this.priceChange == BigDecimal.ZERO) numEmpty++
		if ((this.homeOwnerIns) && this.homeOwnerIns == 0) numEmpty++
		if ((this.insPeriod) && this.insPeriod == "") numEmpty++
		if ((this.walkScore) && this.walkScore.value == 0) numEmpty++
		if ((this.trashSvc) && this.trashSvc == "") numEmpty++
		if ((this.mortRate) && this.mortRate == BigDecimal.ZERO) numEmpty++
		if ((this.mortTerm) && this.mortTerm.value == 0) numEmpty++
		if ((this.avgTaxes) && this.avgTaxes == 0) numEmpty++
		if ((this.lastSoldPrice) && this.lastSoldPrice.value == 0) numEmpty++
		if ((this.lastSoldDate) && this.lastSoldDate == zeroDate) numEmpty++
		if ((this.dateCreated) && this.dateCreated == zeroDate) numEmpty++
		if ((this.lastUpdated) && this.lastUpdated == zeroDate) numEmpty++
		if ((this.road) && this.road == 0) numEmpty++
		if ((this.utilities) && this.utilities == "") numEmpty++
		if ((this.deckOrBalcony) && this.deckOrBalcony == 0) numEmpty++
		if ((this.foundation) && this.foundation == "") numEmpty++
		if ((this.source) && this.source == "") numEmpty++
		if ((this.floodZone) && this.floodZone == 0) numEmpty++
		
		if ((otherProperty.version) && otherProperty.version.value == 0) numOtherEmpty++
		if ((otherProperty.address_id) && otherProperty.address_id.value == 0) numOtherEmpty++
		if ((otherProperty.mls) && otherProperty.mls == "") numOtherEmpty++
		if ((otherProperty.property_tax) && otherProperty.property_tax.intVal == 0) numOtherEmpty++
		if ((otherProperty.assessed_value) && otherProperty.assessed_value.intVal == 0) numOtherEmpty++
		if ((otherProperty.days_on_mkt) && otherProperty.days_on_mkt.value == 0) numOtherEmpty++
		if ((otherProperty.district) && otherProperty.district == "") numOtherEmpty++
		if ((otherProperty.features) && otherProperty.features == "") numOtherEmpty++
		if ((otherProperty.internet_svc) && otherProperty.internet_svc == "") numOtherEmpty++
		if ((otherProperty.price_per_sq_ft) && otherProperty.price_per_sq_ft.intVal == 0) numOtherEmpty++
		if ((otherProperty.description) && otherProperty.description == "") numOtherEmpty++
		if ((otherProperty.active) && otherProperty.active.value == 0) numOtherEmpty++
		if ((otherProperty.status) && otherProperty.status == "") numOtherEmpty++
		if ((otherProperty.tax_key) && otherProperty.tax_key == "") numOtherEmpty++
		if ((otherProperty.phone_svc) && otherProperty.phone_svc == "") numOtherEmpty++
		if ((otherProperty.tenure) && otherProperty.tenure == "") numOtherEmpty++
		if ((otherProperty.tv_svc) && otherProperty.tv_svc == "") numOtherEmpty++
		if ((otherProperty.property_type) && otherProperty.property_type == "") numOtherEmpty++
		if ((otherProperty.beds) && otherProperty.beds.value == 0) numOtherEmpty++
		if ((otherProperty.baths) && otherProperty.baths.value == 0) numOtherEmpty++
		if ((otherProperty.halfbaths) && otherProperty.halfbaths.value == 0) numOtherEmpty++
		if ((otherProperty.house_size) && otherProperty.house_size.value == 0) numOtherEmpty++
		if ((otherProperty.house_size_units) && otherProperty.house_size_units == "") numOtherEmpty++
		if ((otherProperty.parking_spaces) && otherProperty.parking_spaces.value == 0) numOtherEmpty++
		if ((otherProperty.parking_type) && otherProperty.parking_type == "") numOtherEmpty++
		if ((otherProperty.power) && otherProperty.power == "") numOtherEmpty++
		if ((otherProperty.sewer) && otherProperty.sewer == "") numOtherEmpty++
		if ((otherProperty.short_sale) && otherProperty.short_sale == 0) numOtherEmpty++
		if ((otherProperty.pre_foreclosure) && otherProperty.pre_foreclosure == 0) numOtherEmpty++
		if ((otherProperty.reo) && otherProperty.reo == "") numOtherEmpty++
		if ((otherProperty.year_built) && otherProperty.year_built == zeroDate) numOtherEmpty++
		if ((otherProperty.water) && otherProperty.water == "") numOtherEmpty++
		if ((otherProperty.condo_project) && otherProperty.condo_project == "") numOtherEmpty++
		if ((otherProperty.zoning) && otherProperty.zoning == "") numOtherEmpty++
		if ((otherProperty.topography) && otherProperty.topography == "") numOtherEmpty++
		if ((otherProperty.lot_size) && otherProperty.lot_size == 0) numOtherEmpty++
		if ((otherProperty.lot_size_units) && otherProperty.lotSizeUnits == "") numOtherEmpty++
		if ((otherProperty.fencing) && otherProperty.fencing == "") numOtherEmpty++
		if ((otherProperty.frontage) && otherProperty.frontage == "") numOtherEmpty++
		if ((otherProperty.hoa_dues_per_month) && otherProperty.hoa_dues_per_month == BigDecimal.ZERO) numOtherEmpty++
		if ((otherProperty.appliances) && otherProperty.appliances == "") numOtherEmpty++
		if ((otherProperty.maintenance_fee) && otherProperty.maintenance_fee == BigDecimal.ZERO) numOtherEmpty++
		if ((otherProperty.design) && otherProperty.design == "") numOtherEmpty++
		if ((otherProperty.driveway) && otherProperty.driveway == "") numOtherEmpty++
		if ((otherProperty.floor_covering) && otherProperty.floor_covering == "") numOtherEmpty++
		if ((otherProperty.heating_type) && otherProperty.heating_type == "") numOtherEmpty++
		if ((otherProperty.cooling_type) && otherProperty.cooling_type == "") numOtherEmpty++
		if ((otherProperty.other_features) && otherProperty.other_features == "") numOtherEmpty++
		if ((otherProperty.roof_type) && otherProperty.roof_type == "") numOtherEmpty++
		if ((otherProperty.window_coverings) && otherProperty.window_coverings == "") numOtherEmpty++
		if ((otherProperty.kitchen_type) && otherProperty.kitchen_type == "") numOtherEmpty++
		if ((otherProperty.counter_tops) && otherProperty.counter_tops == "") numOtherEmpty++
		if ((otherProperty.heating_cooling) && otherProperty.heating_cooling == "") numOtherEmpty++
		if ((otherProperty.solid_waste_disposal) && otherProperty.solid_waste_disposal == "") numOtherEmpty++
		if ((otherProperty.lot_desc) && otherProperty.lot_desc == "") numOtherEmpty++
		if ((otherProperty.sub_division) && otherProperty.sub_division == "") numOtherEmpty++
		if ((otherProperty.amenities) && otherProperty.amenities == "") numOtherEmpty++
		if ((otherProperty.basement) && otherProperty.basement == 0) numOtherEmpty++
		if ((otherProperty.basement_finished_pct) && otherProperty.basement_finished_pct == BigDecimal.ZERO) numOtherEmpty++
		if ((otherProperty.num_fireplaces) && otherProperty.num_fireplaces.value == 0) numOtherEmpty++
		if ((otherProperty.attic) && otherProperty.attic == 0) numOtherEmpty++
		if ((otherProperty.exterior_material) && otherProperty.exterior_material == "") numOtherEmpty++
		if ((otherProperty.view) && otherProperty.view == "") numOtherEmpty++
		if ((otherProperty.cable_ready) && otherProperty.cable_ready == 0) numOtherEmpty++
		if ((otherProperty.elevator) && otherProperty.elevator == 0) numOtherEmpty++
		if ((otherProperty.hot_tub_spa) && otherProperty.hot_tub_spa == 0) numOtherEmpty++
		if ((otherProperty.pool) && otherProperty.pool == 0) numOtherEmpty++
		if ((otherProperty.sauna) && otherProperty.sauna == 0) numOtherEmpty++
		if ((otherProperty.sec_system) && otherProperty.sec_system == "") numOtherEmpty++
		if ((otherProperty.room_types) && otherProperty.room_types == "") numOtherEmpty++
		if ((otherProperty.elementary_school) && otherProperty.elementary_school == "") numOtherEmpty++
		if ((otherProperty.high_school) && otherProperty.high_school == "") numOtherEmpty++
		if ((otherProperty.middle_school) && otherProperty.middle_school == "") numOtherEmpty++
		if ((otherProperty.school_district) && otherProperty.school_district == "") numOtherEmpty++
		if ((otherProperty.floor_number) && otherProperty.floor_number == 0) numOtherEmpty++
		if ((otherProperty.num_stories) && otherProperty.num_stories == 0) numOtherEmpty++
		if ((otherProperty.zestimate) && otherProperty.zestimate.value == 0) numOtherEmpty++
		if ((otherProperty.zestimate_last_updated) && otherProperty.zestimate_last_updated == zeroDate) numOtherEmpty++
		if ((otherProperty.price) && otherProperty.price == BigDecimal.ZERO) numOtherEmpty++
		if ((otherProperty.original_list_price) && otherProperty.original_list_price == BigDecimal.ZERO) numOtherEmpty++
		if ((otherProperty.price_change) && otherProperty.price_change == BigDecimal.ZERO) numOtherEmpty++
		if ((otherProperty.home_owner_ins) && otherProperty.home_owner_ins == 0) numOtherEmpty++
		if ((otherProperty.ins_period) && otherProperty.ins_period == "") numOtherEmpty++
		if ((otherProperty.walk_score) && otherProperty.walk_score.value == 0) numOtherEmpty++
		if ((otherProperty.trash_svc) && otherProperty.trash_svc == "") numOtherEmpty++
		if ((otherProperty.mort_rate) && otherProperty.mort_rate == BigDecimal.ZERO) numOtherEmpty++
		if ((otherProperty.mort_term) && otherProperty.mort_term.value == 0) numOtherEmpty++
		if ((otherProperty.avg_taxes) && otherProperty.avg_taxes == 0) numOtherEmpty++
		if ((otherProperty.last_sold_price) && otherProperty.last_sold_price.value == 0) numOtherEmpty++
		if ((otherProperty.last_sold_date) && otherProperty.last_sold_date == zeroDate) numOtherEmpty++
		if ((otherProperty.create_date) && otherProperty.create_date == zeroDate) numOtherEmpty++
		if ((otherProperty.update_date) && otherProperty.update_date == zeroDate) numOtherEmpty++
		if ((otherProperty.road) && otherProperty.road == 0) numOtherEmpty++
		if ((otherProperty.utilities) && otherProperty.utilities == "") numOtherEmpty++
		if ((otherProperty.deck_or_balcony) && otherProperty.deck_or_balcony == 0) numOtherEmpty++
		if ((otherProperty.foundation) && otherProperty.foundation == "") numOtherEmpty++
		if ((otherProperty.source) && otherProperty.source == "") numOtherEmpty++
		if ((otherProperty.flood_zone) && otherProperty.flood_zone == 0) numOtherEmpty++
	}
	
	public boolean isEquivalent(GroovyRowResult otherProperty) {
		int totalDiffs = 0
		def diffs = ""
		def diffsMap = [:]
		
		if (this.id != otherProperty.id) { 
			diffs = "this id = |${this.id}|\t\t\t other id \t\t= |${otherProperty.id}|\n"
			totalDiffs++
			diffsMap["id"] = 1
			if (debug) { 
				 println(diffs)
			}
		} 
		
		if (this.mls != otherProperty.mls) {
			diffs = "this mls = |${this.mls}|\t\t\t other mls \t\t= |${otherProperty.mls}|\n"
			totalDiffs++
			diffsMap["mls"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.district != otherProperty.district) {
			diffs = "this district = |${this.district}|\t\t\t other district \t\t= |${otherProperty.district}|\n"
			totalDiffs++
			diffsMap["district"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.subDivision != otherProperty.subDivision) {
			diffs = "this subDivision = |${this.subDivision}|\t\t\t other subDivision \t\t= |${otherProperty.subDivision}|\n"
			totalDiffs++
			diffsMap["subDivision"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.beds != otherProperty.beds) {
			diffs = "this beds = |${this.beds}|\t\t\t other beds \t\t= |${otherProperty.beds}|\n"
			totalDiffs++
			diffsMap["beds"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.baths != otherProperty.baths) {
			diffs = "this baths = |${this.baths}|\t\t\t other baths \t\t= |${otherProperty.baths}|\n"
			totalDiffs++
			diffsMap["baths"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.yearBuilt != otherProperty.yearBuilt) {
			diffs = "this yearBuilt = |${this.yearBuilt}|\t\t\t other yearBuilt \t\t= |${otherProperty.yearBuilt}|\n"
			totalDiffs++
			diffsMap["yearBuilt"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.parkingSpaces != otherProperty.parkingSpaces) {
			diffs = "this parkingSpaces = |${this.parkingSpaces}|\t\t\t other parkingSpaces \t\t= |${otherProperty.parkingSpaces}|\n"
			totalDiffs++
			diffsMap["parkingSpaces"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.parkingType != otherProperty.parkingType) {
			diffs = "this parkingType = |${this.parkingType}|\t\t\t other parkingType \t\t= |${otherProperty.parkingType}|\n"
			totalDiffs++
			diffsMap["parkingType"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.daysOnMkt != otherProperty.daysOnMkt) {
			diffs = "this daysOnMkt = |${this.daysOnMkt}|\t\t\t other daysOnMkt \t\t= |${otherProperty.daysOnMkt}|\n"
			totalDiffs++
			diffsMap["daysOnMkt"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.heatingType != otherProperty.heatingType) {
			diffs = "this heatingType = |${this.heatingType}|\t\t\t other heatingType \t\t= |${otherProperty.heatingType}|\n"
			totalDiffs++
			diffsMap["heatingType"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.coolingType != otherProperty.coolingType) {
			diffs = "this coolingType = |${this.coolingType}|\t\t\t other coolingType \t\t= |${otherProperty.coolingType}|\n"
			totalDiffs++
			diffsMap["coolingType"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.basement != otherProperty.basement) {
			diffs = "this basement = |${this.basement}|\t\t\t other basement \t\t= |${otherProperty.basement}|\n"
			totalDiffs++
			diffsMap["basement"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.basementFinishedPct != otherProperty.basementFinishedPct) {
			diffs = "this basementFinishedPct = |${this.basementFinishedPct}|\t\t\t other basementFinishedPct \t\t= |${otherProperty.basementFinishedPct}|\n"
			totalDiffs++
			diffsMap["basementFinishedPct"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.numFireplaces != otherProperty.numFireplaces) {
			diffs = "this numFireplaces = |${this.numFireplaces}|\t\t\t other numFireplaces \t\t= |${otherProperty.numFireplaces}|\n"
			totalDiffs++
			diffsMap["numFireplaces"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.floorCovering != otherProperty.floorCovering) {
			diffs = "this floorCovering = |${this.floorCovering}|\t\t\t other floorCovering \t\t= |${otherProperty.floorCovering}|\n"
			totalDiffs++
			diffsMap["floorCovering"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.attic != otherProperty.attic) {
			diffs = "this attic = |${this.attic}|\t\t\t other attic \t\t= |${otherProperty.attic}|\n"
			totalDiffs++
			diffsMap["attic"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.roofType != otherProperty.roofType) {
			diffs = "this roofType = |${this.roofType}|\t\t\t other roofType \t\t= |${otherProperty.roofType}|\n"
			totalDiffs++
			diffsMap["roofType"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.exteriorMaterial != otherProperty.exteriorMaterial) {
			diffs = "this exteriorMaterial = |${this.exteriorMaterial}|\t\t\t other exteriorMaterial \t\t= |${otherProperty.exteriorMaterial}|\n"
			totalDiffs++
			diffsMap["exteriorMaterial"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.view != otherProperty.view) {
			diffs = "this view = |${this.view}|\t\t\t other view \t\t= |${otherProperty.view}|\n"
			totalDiffs++
			diffsMap["view"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.cableReady != otherProperty.cableReady) {
			diffs = "this cableReady = |${this.cableReady}|\t\t\t other cableReady \t\t= |${otherProperty.cableReady}|\n"
			totalDiffs++
			diffsMap["cableReady"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.elevator != otherProperty.elevator) {
			diffs = "this elevator = |${this.elevator}|\t\t\t other elevator \t\t= |${otherProperty.elevator}|\n"
			totalDiffs++
			diffsMap["elevator"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.hotTubSpa != otherProperty.hotTubSpa) {
			diffs = "this hotTubSpa = |${this.hotTubSpa}|\t\t\t other hotTubSpa \t\t= |${otherProperty.hotTubSpa}|\n"
			totalDiffs++
			diffsMap["hotTubSpa"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.pool != otherProperty.pool) {
			diffs = "this pool = |${this.pool}|\t\t\t other pool \t\t= |${otherProperty.pool}|\n"
			totalDiffs++
			diffsMap["pool"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.sauna != otherProperty.sauna) {
			diffs = "this sauna = |${this.sauna}|\t\t\t other sauna \t\t= |${otherProperty.sauna}|\n"
			totalDiffs++
			diffsMap["sauna"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.secSystem != otherProperty.secSystem) {
			diffs = "this secSystem = |${this.secSystem}|\t\t\t other secSystem \t\t= |${otherProperty.secSystem}|\n"
			totalDiffs++
			diffsMap["secSystem"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.roomTypes != otherProperty.roomTypes) {
			diffs = "this roomTypes = |${this.roomTypes}|\t\t\t other roomTypes \t\t= |${otherProperty.roomTypes}|\n"
			totalDiffs++
			diffsMap["roomTypes"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.elementarySchool != otherProperty.elementarySchool) {
			diffs = "this elementarySchool = |${this.elementarySchool}|\t\t\t other elementarySchool \t\t= |${otherProperty.elementarySchool}|\n"
			totalDiffs++
			diffsMap["elementarySchool"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.highSchool != otherProperty.highSchool) {
			diffs = "this highSchool = |${this.highSchool}|\t\t\t other highSchool \t\t= |${otherProperty.highSchool}|\n"
			totalDiffs++
			diffsMap["highSchool"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.middleSchool != otherProperty.middleSchool) {
			diffs = "this middleSchool = |${this.middleSchool}|\t\t\t other middleSchool \t\t= |${otherProperty.middleSchool}|\n"
			totalDiffs++
			diffsMap["middleSchool"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.schoolDistrict != otherProperty.schoolDistrict) {
			diffs = "this schoolDistrict = |${this.schoolDistrict}|\t\t\t other schoolDistrict \t\t= |${otherProperty.schoolDistrict}|\n"
			totalDiffs++
			diffsMap["schoolDistrict"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.floorNumber != otherProperty.floorNumber) {
			diffs = "this floorNumber = |${this.floorNumber}|\t\t\t other floorNumber \t\t= |${otherProperty.floorNumber}|\n"
			totalDiffs++
			diffsMap["floorNumber"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.numStories != otherProperty.numStories) {
			diffs = "this numStories = |${this.numStories}|\t\t\t other numStories \t\t= |${otherProperty.numStories}|\n"
			totalDiffs++
			diffsMap["numStories"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.zestimate != otherProperty.zestimate) {
			diffs = "this zestimate = |${this.zestimate}|\t\t\t other zestimate \t\t= |${otherProperty.zestimate}|\n"
			totalDiffs++
			diffsMap["zestimate"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.zestimateLastUpdated != otherProperty.zestimateLastUpdated) {
			diffs = "this zestimateLastUpdated = |${this.zestimateLastUpdated}|\t\t\t other zestimateLastUpdated \t\t= |${otherProperty.zestimateLastUpdated}|\n"
			totalDiffs++
			diffsMap["zestimateLastUpdated"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.rentEstimate != otherProperty.rentEstimate) {
			diffs = "this rentEstimate = |${this.rentEstimate}|\t\t\t other rentEstimate \t\t= |${otherProperty.rentEstimate}|\n"
			totalDiffs++
			diffsMap["rentEstimate"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.rentEstimateLastUpdated != otherProperty.rentEstimateLastUpdated) {
			diffs = "this rentEstimateLastUpdated = |${this.rentEstimateLastUpdated}|\t\t\t other rentEstimateLastUpdated \t\t= |${otherProperty.rentEstimateLastUpdated}|\n"
			totalDiffs++
			diffsMap["rentEstimateLastUpdated"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.lotSize != otherProperty.lotSize) {
			diffs = "this lotSize = |${this.lotSize}|\t\t\t other lotSize \t\t= |${otherProperty.lotSize}|\n"
			totalDiffs++
			diffsMap["lotSize"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.lotSizeUnits != otherProperty.lotSizeUnits) {
			diffs = "this lotSizeUnits = |${this.lotSizeUnits}|\t\t\t other lotSizeUnits \t\t= |${otherProperty.lotSizeUnits}|\n"
			totalDiffs++
			diffsMap["lotSizeUnits"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.propertyType != otherProperty.propertyType) {
			diffs = "this propertyType = |${this.propertyType}|\t\t\t other propertyType \t\t= |${otherProperty.propertyType}|\n"
			totalDiffs++
			diffsMap["propertyType"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.price != otherProperty.price) {
			diffs = "this price = |${this.price}|\t\t\t other price \t\t= |${otherProperty.price}|\n"
			totalDiffs++
			diffsMap["price"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.propertyTax != otherProperty.propertyTax) {
			diffs = "this propertyTax = |${this.propertyTax}|\t\t\t other propertyTax \t\t= |${otherProperty.propertyTax}|\n"
			totalDiffs++
			diffsMap["propertyTax"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.assessedValue != otherProperty.assessedValue) {
			diffs = "this assessedValue = |${this.assessedValue}|\t\t\t other assessedValue \t\t= |${otherProperty.assessedValue}|\n"
			totalDiffs++
			diffsMap["assessedValue"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.homeOwnerIns != otherProperty.homeOwnerIns) {
			diffs = "this homeOwnerIns = |${this.homeOwnerIns}|\t\t\t other homeOwnerIns \t\t= |${otherProperty.homeOwnerIns}|\n"
			totalDiffs++
			diffsMap["homeOwnerIns"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.insPeriod != otherProperty.insPeriod) {
			diffs = "this insPeriod = |${this.insPeriod}|\t\t\t other insPeriod \t\t= |${otherProperty.insPeriod}|\n"
			totalDiffs++
			diffsMap["insPeriod"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.description != otherProperty.description) {
			diffs = "this description = |${this.description}|\t\t\t other description \t\t= |${otherProperty.description}|\n"
			totalDiffs++
			diffsMap["description"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.walkScore != otherProperty.walkScore) {
			diffs = "this walkScore = |${this.walkScore}|\t\t\t other walkScore \t\t= |${otherProperty.walkScore}|\n"
			totalDiffs++
			diffsMap["walkScore"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.water != otherProperty.water) {
			diffs = "this water = |${this.water}|\t\t\t other water \t\t= |${otherProperty.water}|\n"
			totalDiffs++
			diffsMap["water"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.condoProject != otherProperty.condoProject) {
			diffs = "this condoProject = |${this.condoProject}|\t\t\t other condoProject \t\t= |${otherProperty.condoProject}|\n"
			totalDiffs++
			diffsMap["condoProject"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.condoProject != otherProperty.condoProject) {
			diffs = "this condoProject = |${this.condoProject}|\t\t\t other condoProject \t\t= |${otherProperty.condoProject}|\n"
			totalDiffs++
			diffsMap["condoProject"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.sewer != otherProperty.sewer) {
			diffs = "this sewer = |${this.sewer}|\t\t\t other sewer \t\t= |${otherProperty.sewer}|\n"
			totalDiffs++
			diffsMap["sewer"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.trashSvc != otherProperty.trashSvc) {
			diffs = "this trashSvc = |${this.trashSvc}|\t\t\t other trashSvc \t\t= |${otherProperty.trashSvc}|\n"
			totalDiffs++
			diffsMap["trashSvc"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.mortRate != otherProperty.mortRate) {
			diffs = "this mortRate = |${this.mortRate}|\t\t\t other mortRate \t\t= |${otherProperty.mortRate}|\n"
			totalDiffs++
			diffsMap["mortRate"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.mortTerm != otherProperty.mortTerm) {
			diffs = "this mortTerm = |${this.mortTerm}|\t\t\t other mortTerm \t\t= |${otherProperty.mortTerm}|\n"
			totalDiffs++
			diffsMap["mortTerm"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.avgTaxes != otherProperty.avgTaxes) {
			diffs = "this avgTaxes = |${this.avgTaxes}|\t\t\t other avgTaxes \t\t= |${otherProperty.avgTaxes}|\n"
			totalDiffs++
			diffsMap["avgTaxes"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.lastSoldPrice != otherProperty.lastSoldPrice) {
			diffs = "this lastSoldPrice = |${this.lastSoldPrice}|\t\t\t other lastSoldPrice \t\t= |${otherProperty.lastSoldPrice}|\n"
			totalDiffs++
			diffsMap["lastSoldPrice"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.lastSoldDate != otherProperty.lastSoldDate) {
			diffs = "this lastSoldDate = |${this.lastSoldDate}|\t\t\t other lastSoldDate \t\t= |${otherProperty.lastSoldDate}|\n"
			totalDiffs++
			diffsMap["lastSoldDate"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.dateCreated != otherProperty.dateCreated) {
			diffs = "this dateCreated = |${this.dateCreated}|\t\t\t other dateCreated \t\t= |${otherProperty.dateCreated}|\n"
			totalDiffs++
			diffsMap["dateCreated"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.lastUpdated != otherProperty.lastUpdated) {
			diffs = "this lastUpdated = |${this.lastUpdated}|\t\t\t other lastUpdated \t\t= |${otherProperty.lastUpdated}|\n"
			totalDiffs++
			diffsMap["lastUpdated"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.utilities != otherProperty.utilities) {
			diffs = "this utilities = |${this.utilities}|\t\t\t other utilities \t\t= |${otherProperty.utilities}|\n"
			totalDiffs++
			diffsMap["utilities"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.tenure != otherProperty.tenure) {
			diffs = "this tenure = |${this.tenure}|\t\t\t other tenure \t\t= |${otherProperty.tenure}|\n"
			totalDiffs++
			diffsMap["tenure"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		
		if (this.hoaDuesPerMonth != otherProperty.hoaDuesPerMonth) {
			diffs = "this hoaDuesPerMonth = |${this.hoaDuesPerMonth}|\t\t\t other hoaDuesPerMonth \t\t= |${otherProperty.hoaDuesPerMonth}|\n"
			totalDiffs++
			diffsMap["hoaDuesPerMonth"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.lotDesc != otherProperty.lotDesc) {
			diffs = "this lotDesc = |${this.lotDesc}|\t\t\t other lotDesc \t\t= |${otherProperty.lotDesc}|\n"
			totalDiffs++
			diffsMap["lotDesc"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.foundation != otherProperty.foundation) {
			diffs = "this foundation = |${this.foundation}|\t\t\t other foundation \t\t= |${otherProperty.foundation}|\n"
			totalDiffs++
			diffsMap["foundation"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.design != otherProperty.design) {
			diffs = "this design = |${this.design}|\t\t\t other design \t\t= |${otherProperty.design}|\n"
			totalDiffs++
			diffsMap["design"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.deckOrBalcony != otherProperty.deckOrBalcony) {
			diffs = "this deckOrBalcony = |${this.deckOrBalcony}|\t\t\t other deckOrBalcony \t\t= |${otherProperty.deckOrBalcony}|\n"
			totalDiffs++
			diffsMap["deckOrBalcony"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.source != otherProperty.source) {
			diffs = "this source = |${this.source}|\t\t\t other source \t\t= |${otherProperty.source}|\n"
			totalDiffs++
			diffsMap["source"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.zoning != otherProperty.zoning) {
			diffs = "this zoning = |${this.zoning}|\t\t\t other zoning \t\t= |${otherProperty.zoning}|\n"
			totalDiffs++
			diffsMap["zoning"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.floodZone != otherProperty.floodZone) {
			diffs = "this floodZone = |${this.floodZone}|\t\t\t other floodZone \t\t= |${otherProperty.floodZone}|\n"
			totalDiffs++
			diffsMap["floodZone"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.topography != otherProperty.topography) {
			diffs = "this topography = |${this.topography}|\t\t\t other topography \t\t= |${otherProperty.topography}|\n"
			totalDiffs++
			diffsMap["topography"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.frontage != otherProperty.frontage) {
			diffs = "this frontage = |${this.frontage}|\t\t\t other frontage \t\t= |${otherProperty.frontage}|\n"
			totalDiffs++
			diffsMap["frontage"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.phoneSvc != otherProperty.phoneSvc) {
			diffs = "this phoneSvc = |${this.phoneSvc}|\t\t\t other phoneSvc \t\t= |${otherProperty.phoneSvc}|\n"
			totalDiffs++
			diffsMap["phoneSvc"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.tvSvc != otherProperty.tvSvc) {
			diffs = "this tvSvc = |${this.tvSvc}|\t\t\t other tvSvc \t\t= |${otherProperty.tvSvc}|\n"
			totalDiffs++
			diffsMap["tvSvc"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.internetSvc != otherProperty.internetSvc) {
			diffs = "this internetSvc = |${this.internetSvc}|\t\t\t other internetSvc \t\t= |${otherProperty.internetSvc}|\n"
			totalDiffs++
			diffsMap["internetSvc"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.pricePerSqFt != otherProperty.pricePerSqFt) {
			diffs = "this pricePerSqFt = |${this.pricePerSqFt}|\t\t\t other pricePerSqFt \t\t= |${otherProperty.pricePerSqFt}|\n"
			totalDiffs++
			diffsMap["pricePerSqFt"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.shortSale != otherProperty.shortSale) {
			diffs = "this shortSale = |${this.shortSale}|\t\t\t other shortSale \t\t= |${otherProperty.shortSale}|\n"
			totalDiffs++
			diffsMap["shortSale"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.preForeclosure != otherProperty.preForeclosure) {
			diffs = "this preForeclosure = |${this.preForeclosure}|\t\t\t other preForeclosure \t\t= |${otherProperty.preForeclosure}|\n"
			totalDiffs++
			diffsMap["preForeclosure"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.reo != otherProperty.reo) {
			diffs = "this reo = |${this.reo}|\t\t\t other reo \t\t= |${otherProperty.reo}|\n"
			totalDiffs++
			diffsMap["reo"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.appliances != otherProperty.appliances) {
			diffs = "this appliances = |${this.appliances}|\t\t\t other appliances \t\t= |${otherProperty.appliances}|\n"
			totalDiffs++
			diffsMap["appliances"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.driveway != otherProperty.driveway) {
			diffs = "this driveway = |${this.driveway}|\t\t\t other driveway \t\t= |${otherProperty.driveway}|\n"
			totalDiffs++
			diffsMap["driveway"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.features != otherProperty.features) {
			diffs = "this features = |${this.features}|\t\t\t other features \t\t= |${otherProperty.features}|\n"
			totalDiffs++
			diffsMap["features"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.otherFeatures != otherProperty.otherFeatures) {
			diffs = "this otherFeatures = |${this.otherFeatures}|\t\t\t other otherFeatures \t\t= |${otherProperty.otherFeatures}|\n"
			totalDiffs++
			diffsMap["otherFeatures"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.maintenanceFee != otherProperty.maintenanceFee) {
			diffs = "this maintenanceFee = |${this.maintenanceFee}|\t\t\t other maintenanceFee \t\t= |${otherProperty.maintenanceFee}|\n"
			totalDiffs++
			diffsMap["maintenanceFee"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (this.windowCoverings != otherProperty.windowCoverings) {
			diffs = "this windowCoverings = |${this.windowCoverings}|\t\t\t other windowCoverings \t\t= |${otherProperty.windowCoverings}|\n"
			totalDiffs++
			diffsMap["windowCoverings"] = 1
			if (debug) {
				 println(diffs)
			}
		}
		
		if (diffsMap["addressId"] || diffsMap["beds"] || diffsMap["baths"] || diffsMap["price"] || diffsMap["houseSize"]) {
			return false
		} else {
		    return true
		}
	}
}
