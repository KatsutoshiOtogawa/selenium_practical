package com.example;

import java.io.IOException;

public interface StorageInterface {

    public void transport(String uri) throws IOException,InterruptedException,Exception;
    public void createShopItemIdPath();
}