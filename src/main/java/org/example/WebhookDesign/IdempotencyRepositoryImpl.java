package org.example.WebhookDesign;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Repository;

@Repository
public class IdempotencyRepositoryImpl implements IdempotencyRepository{
    public record Record(String status, long createdAt, String downstreamCallId) {}
    private final ConcurrentMap<String, Record> store = new ConcurrentHashMap<>();

    @Override
    public LockStatus tryAcquire(String eventId) {
        Record current = store.putIfAbsent(eventId, new Record("IN_PROGRESS", System.currentTimeMillis(), null));
        if (current == null) {
            return LockStatus.ACQUIRED;
        }
        if ("IN_PROGRESS".equals(current.status())) {
            return LockStatus.ALREADY_IN_PROGRESS;
        }
        return LockStatus.ALREADY_COMPLETED;
    }

    @Override
    public void markCompleted(String eventId, String downstreamCallId) {
        store.put(eventId, new Record("COMPLETED", System.currentTimeMillis(), downstreamCallId));
    }

    @Override
    public void releaseLockOnFailure(String eventId) {
        store.remove(eventId);
    }
}
