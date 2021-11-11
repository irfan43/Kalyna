package com.ok.kalyna;

public class ToDelete {

    public static void main(String[] args) {

        String after = "02000200020002000200020002000200";
        String before = "882F21653D775DA1D25CBE2F3AE2DA7D";
        byte[][] key = KalynaUtil.hexToState("000102030405060708090A0B0C0D0E0F");

        //byte[][] key = KalynaRoundFunction.subRoundKey( KalynaUtil.hexToState(before),KalynaUtil.hexToState(after) );
        byte[][] out = KalynaRoundFunction.addRoundKey(key,KalynaRoundFunction.addRoundKey(
                KalynaUtil.hexToState("16505E6B9B3AB1E6865B77DCE082A0F4")  , KalynaUtil.hexToState("02000200020002000200020002000200")
        ));
        System.out.println("got :-\n" + KalynaUtil.byteArrayToHex(out));
//        byte[] box = new byte[256];
//        Scanner scn = new Scanner(System.in);
//        List<String> elems = new ArrayList<>();
//
//        StringBuilder built = new StringBuilder();
//        while(true) {
//            String tmp = scn.nextLine();
//            if(tmp.equals(""))
//                break;
//            tmp = tmp.toUpperCase();
//            char[] ar = tmp.toCharArray();
//            for(char a: ar){
//                if( (a >= '0' && a <= '9') || (a >= 'A' && a <= 'F') ){
//                    built.append(a);
//                }else{
//                    elems.add(built.toString());
//                    built = new StringBuilder();
//                }
//            }
//            elems.add(built.toString());
//            built = new StringBuilder();
//        }
//
//        System.out.println("{");
//        for (int i = 0;i < 256;i++) {
//            String e = elems.get(i);
//            System.out.print(" (byte)0x" + e + ", ");
//            if(i%16 == 15)
//                System.out.println();
//        }
    }
}
