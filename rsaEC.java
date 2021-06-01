
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Scanner;

public class rsaEC{
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        RSA_e_c r = new RSA_e_c(512);
        r.generate_parameters();
        System.out.println("p = " + r.p);
        System.out.println("q = " + r.q);
        System.out.println("public key = " + r.e);
        System.out.println("private key_2 = " + r.d);
        System.out.println("n = " + r.n);

        BigInteger[] xy;
        String message = sc.nextLine();
        byte[] mas = message.getBytes();

        byte[] myArray1 = Arrays.copyOfRange(mas, 0, mas.length / 2);
        byte[] myArray2 = Arrays.copyOfRange(mas, mas.length / 2, mas.length);

        BigInteger x = new BigInteger(myArray1);
        BigInteger y = new BigInteger(myArray2);

        System.out.println("Открытый текст");
        System.out.println(x);
        System.out.println(y);
        System.out.println("Закрытый текст");
        xy = r.encript(x, y);
        System.out.println(xy[0]);
        System.out.println(xy[1]);
        System.out.println("Расшифрованный текст");
        xy = r.decript(xy);
        System.out.println(bigintegertostring(xy[0]) + bigintegertostring(xy[1]) );
    }


    static BigInteger stringtobiginteger(String str){
        byte[] mas = str.getBytes();
        BigInteger i = new BigInteger(mas);
        return i;
    }

    static String bigintegertostring(BigInteger number){
        byte[] mas = number.toByteArray();
        String a = new String(mas);
        return a;
    }
}

     class RSA_e_c{
    BigInteger p, q;
    BigInteger e, d;
    BigInteger n;
    int length;
  
    RSA_e_c(int length){
        this.length = length / 2;
    }

    public void generate_parameters(){
        p = get_prime();
        q = get_prime();
        e = generate_public_key(p,q);
        d = generate_private_key(p, q, e);
        n = p.multiply(q);
        c(e, d, p, q);
    }

    public void distinct(BigInteger p, BigInteger q){
        if(p.equals(q))
            generate_parameters();
    }

    public  BigInteger generate_private_key(BigInteger p,  BigInteger q, BigInteger e){ // d = e ^-1 mod(p+1)(q+1)
        BigInteger m = (p.add(BigInteger.ONE)).multiply(q.add(BigInteger.ONE));
        try{
            d = e.modInverse(m);
        } catch(ArithmeticException a){
            generate_parameters();
        }
        return d;
    }

    public void c(BigInteger e, BigInteger d, BigInteger p, BigInteger q){
        p = p.add(BigInteger.ONE);
        q = q.add(BigInteger.ONE);
        n = p.multiply(q);
        if(((e.multiply(d)).mod(n)).equals(BigInteger.ONE))
            System.out.println("ed=1mod(n)");
        else
            System.out.println("ed != 1mod(n)");
    }

    public  BigInteger generate_public_key(BigInteger p, BigInteger q){
        BigInteger e = BigInteger.ONE;
        boolean flag_1 = true, flag_2 = true;
        p = p.add(BigInteger.ONE);
        q = q.add(BigInteger.ONE);
        BigInteger mul = p.multiply(q);
        while(flag_1 == true && flag_2 == true){
            if(!e.equals(mul)){
                e = e.add(BigInteger.ONE);
                if(gcd(e, p).equals(BigInteger.ONE))
                    flag_1 = false;
                if(gcd(e, q).equals(BigInteger.ONE))
                    flag_2 = false;
            }
        }
        return e;
    }

    private  BigInteger gcd(BigInteger b1, BigInteger b2) {
        BigInteger gcd = b1.gcd(b2);
        return gcd;
    }

    public  BigInteger get_prime() {
        boolean flag = false;
        BigInteger p = BigInteger.ZERO;

        while(flag == false){
            p = prime();
            flag = prime_mod(p);
        }
        return p;
    }

    public  BigInteger prime() {
        SecureRandom random = new SecureRandom();
        BigInteger prime = BigInteger.probablePrime(length, random);
        return prime;
    }

    public  boolean prime_mod(BigInteger prime){ 
        BigInteger mod = new BigInteger("3");
        BigInteger two = new BigInteger("2");

        BigInteger result = prime.mod(mod);
        if(result.equals(two))
            return true;
        return false;
    }

    public BigInteger calculation_b(BigInteger x, BigInteger y, BigInteger n){
        BigInteger b = (y.pow(2)).subtract(x.pow(3));
        return b.mod(n);
    }

    public BigInteger[] encript(BigInteger x,BigInteger y){
        BigInteger[] xy = new BigInteger[2];

        BigInteger b = calculation_b(x, y, n);

        xy[0] = x;
        xy[1] = y;

        xy = mult_e(xy, e);

        return xy;
    }

    public BigInteger[] mult_e(BigInteger[] xy, BigInteger e){
        xy[0] = xy[0].multiply(e);
        xy[1] = xy[1].multiply(e);

        return xy;
    }



    public BigInteger[] decript(BigInteger[] xy){

        BigInteger x = xy[0];
        BigInteger y = xy[1];

        xy = mult_e(xy, d);
        x = xy[0].mod(n);
        y = xy[1].mod(n);
        xy[0] = x;
        xy[1] = y;
        return xy;
    }

}
