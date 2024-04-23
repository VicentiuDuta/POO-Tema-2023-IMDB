package org.example;

public interface RequestsManager {
    void createRequest(String requestType, String requestDescription, String username);
    void removeRequest(Request r);
}
