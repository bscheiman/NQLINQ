package org.nqlinq.helpers;

import java.lang.reflect.Array;

public class HashCodeHelper {
    public static final int SEED = 23;

    public static int hash( int Seed, boolean value ) {
        return firstTerm( Seed ) + ( value ? 1 : 0 );
    }

    public static int hash(boolean value ) {
        return hash(SEED, value);
    }

    public static int hash( int Seed, char value ) {
        return firstTerm( Seed ) + (int)value;
    }

    public static int hash(char value ) {
        return hash(SEED, value);
    }

    public static int hash( int Seed , int value ) {
        System.out.println("int...");
        return firstTerm( Seed ) + value;
    }

    public static int hash(int value ) {
        System.out.println("int...");
        return firstTerm( SEED ) + value;
    }

    public static int hash( int Seed , long value ) {
        System.out.println("long...");
        return firstTerm(Seed)  + (int)( value ^ (value >>> 32) );
    }
    public static int hash(long value ) {
        return hash(SEED, value);
    }

    public static int hash( int Seed , float value ) {
        return hash( Seed, Float.floatToIntBits(value) );
    }
    public static int hash(float value ) {
        return hash(SEED, value);
    }

    public static int hash( int Seed , double value ) {
        return hash( Seed, Double.doubleToLongBits(value) );
    }

    public static int hash(double value ) {
        return hash(SEED, value);
    }

    public static int hash(Object value ) {
        return hash(SEED, value);
    }

    public static int hash( int Seed , Object value ) {
        int result = Seed;
        if ( value == null) {
            result = hash(result, 0);
        }
        else if ( ! isArray(value) ) {
            result = hash(result, value.hashCode());
        }
        else {
            int length = Array.getLength(value);
            for ( int idx = 0; idx < length; ++idx ) {
                Object item = Array.get(value, idx);
                result = hash(result, item);
            }
        }
        return result;
    }

    private static final int fODD_PRIME_NUMBER = 37;

    private static int firstTerm( int Seed ){
        return fODD_PRIME_NUMBER * Seed;
    }

    private static boolean isArray(Object value){
        return value.getClass().isArray();
    }
}
