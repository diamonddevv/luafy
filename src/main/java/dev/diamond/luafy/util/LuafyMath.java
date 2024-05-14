package dev.diamond.luafy.util;

public class LuafyMath {
    public static long longpow(long n, int pow) {
        if (pow == 0) return 1;
        long result = n;
        while (pow > 1) {
            result *= n;
            pow--;
        }
        return result;
    }
}
