package org.example.WebhookDesign;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LeadSyncService {

    private static final Logger log = LoggerFactory.getLogger(LeadSyncService.class);

    private final IdempotencyRepository idempotencyRepo;
    private final BlueMachinesCampaignClient campaignClient;

    public LeadSyncService(IdempotencyRepository idempotencyRepo, BlueMachinesCampaignClient campaignClient) {
        this.idempotencyRepo = idempotencyRepo;
        this.campaignClient = campaignClient;
    }

    public void processEvent(RequestDto event) {
        String maskedPhone = maskPhoneNumber(event.phone());
        log.info("Processing inbound lead event: event_id={}, candidate_id={}, phone={}",
                event.eventId(), event.candidateId(), maskedPhone);

        IdempotencyRepository.LockStatus lock = idempotencyRepo.tryAcquire(event.eventId());


        if (lock == IdempotencyRepository.LockStatus.ALREADY_IN_PROGRESS) {
            log.warn("Event {} is currently being processed by another worker", event.eventId());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Concurrent duplicate event processing");
        }

        if (lock == IdempotencyRepository.LockStatus.ALREADY_COMPLETED) {
            log.info("Event {} was already processed successfully. Suppressing side effects.", event.eventId());
            return;
        }

        try {
            BlueMachinesCampaignClient.CampaignResponse response = campaignClient.triggerCampaignWithRetry(event);
            idempotencyRepo.markCompleted(event.eventId(), response.campaign_id());
            log.info("Successfully triggered campaign for event_id={}, bm_campaign_id={}",
                    event.eventId(), response.campaign_id());
        } catch (Exception ex) {
            idempotencyRepo.releaseLockOnFailure(event.eventId());
            log.error("Releasing lock for event_id={} due to downstream failure", event.eventId(), ex);
            throw ex;
        }
    }

    public String maskPhoneNumber(String phone) {
        if (phone == null || phone.length() < 7) {
            return "***";
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }
}