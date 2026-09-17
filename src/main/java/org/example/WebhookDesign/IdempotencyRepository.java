package org.example.WebhookDesign;

public interface IdempotencyRepository {
    enum LockStatus { ACQUIRED, ALREADY_IN_PROGRESS, ALREADY_COMPLETED }
    LockStatus tryAcquire(String eventId);
    void markCompleted(String eventId, String downstreamCallId);
    void releaseLockOnFailure(String eventId);
}
