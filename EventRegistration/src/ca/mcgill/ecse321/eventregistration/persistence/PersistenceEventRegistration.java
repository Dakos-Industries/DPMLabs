package ca.mcgill.ecse321.eventregistration.persistence;

import java.util.Iterator;

import ca.mcgill.ecse321.eventregistration.model.Event;
import ca.mcgill.ecse321.eventregistration.model.Participant;
import ca.mcgill.ecse321.eventregistration.model.Registration;
import ca.mcgill.ecse321.eventregistration.model.RegistrationManager;

public class PersistenceEventRegistration {
	
	private static String filename = "data.xml";
	
	public static void setFilename(String fn) {
		filename = fn;
		}
	
	private static void initializeXStream(){
		PersistenceXStream.setFilename(/*filename + */"eventregistration.xml");
		PersistenceXStream.setAlias("event", Event.class);
		PersistenceXStream.setAlias("participant",Participant.class);
		PersistenceXStream.setAlias("registration", Registration.class);
		PersistenceXStream.setAlias("manager", RegistrationManager.class);
		
	}
	
	public static void loadEventRegistrationModel(){
		RegistrationManager rm = RegistrationManager.getInstance();
		PersistenceEventRegistration.initializeXStream();
		RegistrationManager rm2 = (RegistrationManager) PersistenceXStream.loadFromXMLwithXStream();
		if (rm2 != null){
			// unfortunatly, this creates a second RegistrationManager object, even though it is a singleton
			// copy loaded into singleton instance of RegistrationManager, because this will be used throughout the application
			Iterator<Participant> pIt = rm2.getParticipants().iterator();
			while (pIt.hasNext())
				rm.addParticipant(pIt.next());
			Iterator<Event> eIt = rm2.getEvents().iterator();
			while (eIt.hasNext())
				rm.addEvent(eIt.next());
			Iterator<Registration> rIt = rm2.getRegistrations().iterator();
			while (rIt.hasNext())
				rm.addRegistration(rIt.next());
		}
		
	}

}
