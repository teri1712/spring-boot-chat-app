package com.decade.practice.utils;

import java.util.Base64;

public class Media {

        private static final String ONE_PIXEL_BMP_BASE64 =
                "Qk06AAAAAAAAADYAAAAoAAAAAQAAAAEAAAABABgAAAAAAAQAAAATCwAAEwsAAAAAAAAAAAD///8A";

        public static final byte[] ONE_PIXEL_BMP_BYTES = Base64.getDecoder().decode(ONE_PIXEL_BMP_BASE64);

}
