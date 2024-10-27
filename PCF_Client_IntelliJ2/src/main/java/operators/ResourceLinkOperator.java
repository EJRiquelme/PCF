package operators;

import communication.ConnectionToServer;
import communication.ListenServer;
import communication.SharedStore;
import objects.ResourceLink;

public class ResourceLinkOperator {
    private ConnectionToServer connectionToServer;
    private ListenServer listener;
    private SharedStore sharedStore;

    private String[] finalArguments;

    public ResourceLinkOperator(ConnectionToServer connectionToServer, ListenServer listener) {
        this.connectionToServer = connectionToServer;
        this.listener = listener;
        this.sharedStore = SharedStore.getInstance();
    }

    public void saveLinkData(String[] serverArguments){
        ResourceLink resourceLink = new ResourceLink(sharedStore.getSelectedResource(), serverArguments[1]);
        sharedStore.setSelectedResource(resourceLink);
    }
}
