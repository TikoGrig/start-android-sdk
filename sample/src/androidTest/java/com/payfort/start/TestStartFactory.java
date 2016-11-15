package com.payfort.start;

import com.payfort.start.sample.support.TestStartApiFactory;

/**
 * Factory for {@link Start}.
 */
public class TestStartFactory {

    public static Start newOfflineStart() {
        return new Start(TestStartApiFactory.newOfflineStartApi());
    }

    public static Start new400ErrorsStart() {
        return new Start(TestStartApiFactory.new400ErrorsStartApi());
    }
}
