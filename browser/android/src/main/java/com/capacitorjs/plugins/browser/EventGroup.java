package com.capacitorjs.plugins.browser;

/**
 * Simple class to handle indeterminate sequence of events. Not thread safe.
 */
class EventGroup {

    interface EventGroupCompletion {
        void onGroupCompletion();
    }

    private int count;
    private boolean isComplete;
    private EventGroupCompletion completion;

    public EventGroup(EventGroupCompletion onCompletion) {
        super();
        count = 0;
        isComplete = false;
        completion = onCompletion;
    }

    public void enter() {
        count++;
    }

    public void leave() {
        count--;
        checkForCompletion();
    }

    public void reset() {
        count = 0;
        isComplete = false;
    }

    private void checkForCompletion() {
        if (count <= 0) {
            if (isComplete == false && completion != null) {
                completion.onGroupCompletion();
            }
            isComplete = true;
        }
    }
}
