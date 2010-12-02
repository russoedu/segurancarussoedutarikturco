package util;

import java.security.SecureRandom;

import fase2.Keccak;
import interfaces.SpongeRandom;
import fase2.SpongePRNG;

public class Random {
        
        private static final int SEED_LENGTH = 32;
        private static final int HASH_BITRATE = 576;
        private static final int HASH_DIVERSIFIER = 64;
        
        /**
         * Gets a pseudo random number.
         * @param length the length of the number in bytes.
         * @return
         * A byte array with the generated number.
         */
        public static byte[] getRandomNumber(int length) {
                byte[] seed = SecureRandom.getSeed(SEED_LENGTH);
                
                Keccak k = new Keccak();
                
                k.setBitRate(HASH_BITRATE);
                k.setDiversifier(HASH_DIVERSIFIER);
                
                // Instantiate a sponge object with a Keccak instance for the calculation of r. 
                SpongeRandom sponge = new SpongePRNG(k);
                sponge.init(0);
                sponge.feed(seed, seed.length);
                
                return sponge.fetch(new byte[length], length);
        }
}