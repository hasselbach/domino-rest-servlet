package ch.hasselba.dominorestservlet;

import ch.hasselba.concurrent.pool.NotesThreadPool;
import ch.hasselba.concurrent.thread.NotesThreadFactory;

public class ServiceLocator extends ch.hasselba.service.ServiceLocator {

	public ServiceLocator() {
		NotesThreadFactory notesThreadFactory = new NotesThreadFactory(

				"Lotus Notes Threadpool", "serverName");

		this.notesThreadPool = new NotesThreadPool(1, 8, "Lotus Notes Threadpool", notesThreadFactory);
	}

}
