package com.batko.cinematicketbooking.infrastructure.data.exception;

public class StorageException extends RepositoryException {

  public StorageException(String message) {
    super(message);
  }

  public StorageException(String message, Throwable cause) {
    super(message, cause);
  }
}
