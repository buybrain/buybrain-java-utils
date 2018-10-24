package io.buybrain.util;

import lombok.Value;

@Value
public class Tuple2<A, B> {
    A first;
    B second;
}
