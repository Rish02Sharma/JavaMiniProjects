package org.example.designPatterns.behavioural.observer.notifymefeature.observable;

import org.example.designPatterns.behavioural.observer.notifymefeature.observer.StockNotificationObserver;

// Observable interface
public interface StockAvailabilityObservable {
    void addStockObserver(StockNotificationObserver observer);

    void removeStockObserver(StockNotificationObserver observer);

    void notifyStockObservers();

    boolean purchase(int quantity);

    void restock(int quantity);
}
