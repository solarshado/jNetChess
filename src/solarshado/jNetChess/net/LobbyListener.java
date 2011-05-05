package solarshado.jNetChess.net;

/**
 * A 'call-back' for Lobby to notify interested objects that it has an
 * established connection
 * 
 * @author Adrian Todd
 */
public interface LobbyListener {
    /**
     * Called by Lobby when it has successfully created a {@link RemoteConnection} object.
     * @param rc the {@link RemoteConnection} object that was created
     */
    public void gotConnection(RemoteConnection rc);
}
