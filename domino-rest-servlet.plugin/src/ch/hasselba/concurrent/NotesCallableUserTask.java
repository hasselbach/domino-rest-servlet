package ch.hasselba.concurrent;

import static ec2017.rest.Utils.createUserSession;
import static ec2017.rest.Utils.isEmpty;

import ch.hasselba.domino.GC;
import lotus.domino.Session;

public abstract class NotesCallableUserTask<T> extends NotesCallableTask<T> {

	private String username;

	@SuppressWarnings("unused")
	private NotesCallableUserTask() {
	}

	public NotesCallableUserTask(final String userName) {
		super();
		this.username = userName;
	}

	@Override
	public T call() throws Exception {

		if (isEmpty(username))
			throw new RuntimeException("Username is empty or null.");

		Session session = null;
		exception = null;
		T result = null;
		try {
			session = createUserSession(username);
			result = this.callNotes(session);
		} finally {
			session = GC.recycle(session);
		}

		return result;

	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}