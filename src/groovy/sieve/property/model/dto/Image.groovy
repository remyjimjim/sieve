package sieve.property.model.dto

import java.sql.*;
import java.text.SimpleDateFormat;

class Image implements java.io.Serializable {
	String pattern = "MM/dd/yyyy";
	SimpleDateFormat format = new SimpleDateFormat(pattern);
	Timestamp 		zeroDate = new java.sql.Timestamp(0)
	Integer			id	= new Integer("0")
	Integer			version = new Integer("0")
	Integer			propertyId = new Integer("0")	//(FK to Property.ID)
	Integer			size	= new Integer("0")
	byte[] 			content = [] 
	int				width = 0
	int				height = 0
	Timestamp		createDate
	Timestamp		updateDate
	String			updateUser	= "script"
	String			type = ""
	
	public String toString() {
		return """ImageBean:
		id 					--	${id}
		version				--  ${version}
		propertyId 			--	${propertyId}
		size				--  ${size}
		content				--  ${content}
		width				--  ${width}
		height				--  ${height}
		createDate 			--	${createDate}
		updateDate 			--	${updateDate}
		updateUser 			--	${updateUser}
		type 				--	${type}
		"""
	}
	//image				--  ${image}
}

