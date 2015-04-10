package testschemaexport

class TestOneController {

	
    def index() { 
		def myClasses = grailsApplication.getAllClasses().toString()
		
		render """The classes are:
			${myClasses}
		"""
	}
	
}
