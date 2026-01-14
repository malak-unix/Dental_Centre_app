package ma.dentalTech.tools;

import ma.dentalTech.service.modules.auth.impl.PasswordEncoderImpl;

public class GenerateBcrypt {
    public static void main(String[] args) {
        PasswordEncoderImpl enc = new PasswordEncoderImpl();
        String hash = enc.encode("123456");
        System.out.println("BCrypt(123456) = " + hash);
    }
}
