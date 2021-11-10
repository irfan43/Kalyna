package com.ok.kalyna;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class KalynaRoundFunctionTest {

    private final String[] SBoxTestCaseInput = {
            "050102030405060708090A0B0C0D0E0F",
            "62C87E6D6EBA4734E5583BDD28CACF8D",
            "53E95E9206C5D09BBF6097DCA8989844",
            "87302268407C62A8D965C73A45EFE78C",
            "090102030405060708090A0B0C0D0E0F101112131415161718191A1B1C1D1E1F",
            "81B971FDF46C29BC46EA7A2721CCE2F60C2A3CDFA70950DC16E826FDE2FC790F",
            "D9067442F75BCC4BF96A0DA25E3BF2AEB5B2E8624AD8DBE07F32B0E520FA5AA6",
            "1A1A8F1FAE700D529717DE556A8C2994001B45E76EE4FBC9B4BF888E72D5CD17",
            "59880240370E1D94FA9F45BE79F53091D83B1256B5855CA65B394D19A905B32808AA20C8A9633668B973487888BAB16077AEC524D854785679AAF810CF6FE164",
            "567166BA0A8F6CA189EFF94B39BFF708BB6A6DED340AA6106DB3CDB54BF7E59CA8317B8729C09A6A800BE3D87CDFF4E18FC995A49201F57301A50EBDE517A0D5",
            "B0AA801428C79FBF00C392E2EF2062A5472DAAFDE5C3EE9AC86C323AE0863B4454362963BFB98BA7D26B6CAB0D2BD0C0668B2DD3F6263137DF3FD2BCAF532597",
            "6FB7EECBBB801113A89B457208F86167033DB442B3D3EAEAC8A9BD381D7E4F9B31F536F66C313D325192ED0BFE1F6728C211D77A93DAA3964ECBBFAA3E8922C9"
    };

    private final String[] SBoxTestCaseOutput = {
            "75BB9A4D6BCB452A713ADFB31790511F",
            "FC4F5E9CC3232E40D98141FC1FD382BF",
            "5A04E6B66C846B1D26E724A5C50B28E5",
            "467CC09BDCA48F49074589CEE4597F21",
            "DFBB9A4D6BCB452A713ADFB31790511F6D152B3DC91CBB83795C71D56F5716BD",
            "4C0C0B1B4E89A1697AC6B111DDE2134C17024CEFA03A10A52CECAD1B64C7FD1F",
            "0713146E2AD94B852579DCF847AEA042E980C515AB3968CB552E735A3E8A1807",
            "973629BDA230DCAE7043E8485B0EA129A8BA0078C3878BA3AE05C72724B36483",
            "65119AB5EE177029E69800E277EDA69FC4AE2BD3E9A2D3070F147D8A02CB3AF271FF499602FDB79BEC6C9267CD23F9723B78BEEDC4CC66D377FF8C228220FF37",
            "23DCB93F8751A42E7D5934851905AAD499797C98D1D68A227B1E64504A6647FBC526E5A0BFF195D89BB2588114AD39BBFF770FF669BB860C433B51A2D943B39D",
            "B3FF872D1FA3837AA828996B8D318F75326DCE1BD928468CFB89C4CEAC5541E59644A1FD260CE9040AE5A4D9F0B16BA7A5CFE0F7A7222C94C8BC7969765B9BC0",
            "1EA846AF992A173DC56A0062712181AA06101A6EBD74E7E3FB4096564B83351D72EDB74C3826DEF3E71B06B3568DE2F2E81563DB3ADE1FEB048543E4BBD5C0A3"
    };

    private final String[] xorRoundKeyInput = {
            "16CEDEE8D9990F9E25B506F042D3B305",
            "32F172C7E2D2E1C93B4D13958FBCE28D",
            "044E672502E945D313F24197773D4547",
            "73EC521DA9BAF9777A44212456FE0215",
            "283D9E2711C75C40337BC7AAE17F3DDB643F5DB7B208F27EBE1346E5383E5D77",
            "265A9ED9588BDE16848906BF840AC22279F7FEFB690BA863738A0AADDB72E3D7",
            "C4F7BE3F60C4A2A584D11113A578A31D927275BB6CF91F07C662E4F2EEC9A349",
            "5387F788E1BA245006B87D90E3FD8C7F0B7AF743CFDA32633B147DF991D5F6AF",
            "541056BC458C8C5334A3511FF017BFB6DF2F9322345BACCE61AF80F6BAF0D8CF1952A486F6C1AB14155911BA99C6B321733B5D72DEB3A3887388A65FA49F4A6A",
            "94A272EE19ED0CCBA3C3E5DEE99C1EF2A4440BCB092236E30AB39099E3CFCEFC776AFB74BE1288FBF1D4775802A632F38673171ABBDE3010DC819936A88928C1",
            "595B0F13C242CEB10B980C87B0DB3DB5143C1F41EFF055F13BA630DA2E9EB7A1F8B8CEF74B328379DB953706A83B6FDA3820FDE9E779A3E172CD03D6696A4B68",
            "37964F89469782089A1E93875C5E6D0E5BECAA7102AC77B67462D4ECB6E3AD7BE8A1DA0524AE10EC322668E508CBC57960AC28C55AF76813503CFEA1DC4C19EC"
    };

    private final String[] xorRoundKeyInputKey = {
            "E6865B77DCE082A0F416505E6B9B3AB1",
            "7E70876EAE4984768AAAA00A7C93EC42",
            "768AAAA00A7C93EC427E70876EAE4984",
            "45CED4C51E9140F53E7276820F0BD9FE",
            "08E30FCBA169B3C9DCC80DD7801F072CC16C942E36F7DA2647DFD55B352F0852",
            "DF1F1158FD74EEA5C15531C9239038DB26839A9100031FCBB77CFD35F14F73C2",
            "C9239038DB26839A9100031FCBB77CFD35F14F73C2DF1F1158FD74EEA5C15531",
            "CA2AE58A5C656C0ACE0BF0A628FBA9FC3176E710FC11F75DEB9601F95C22FD12",
            "026130064F03E0F2BD4CA854C9A848BE6445FECF00510ADE0C1C4D43F1073D53B163DF01DF01317E9552F262E51947C0FCF2C8D64CB256FB722DA8E24188EABF",
            "2408F2FA312A9374A300773C06BC7C57E369A136ECE1D879C2DFA2A30349F5B8235CD21701AB035C23BF1BF30F8DE233E0F83AC94DF8012703BE4B8A07DA0D56",
            "36ECE1D879C2DFA2A30349F5B8235CD21701AB035C23BF1BF30F8DE233E0F83AC94DF8012703BE4B8A07DA0D562408F2FA312A9374A300773C06BC7C57E369A1",
            "B8DF3967A97C8BFD5C4DECEC5168EEB40BEE562D80D97AD60998659CCD084D5DC33441F000BF7AFAEEEBE431F96D67DEA0E05A5BFFF8B0AA9A38C9857A91EA23"
    };


    private final String[] xorRoundKeyExpectedOutput =  {
            "F048859F05798D3ED1A356AE294889B4",
            "4C81F5A94C9B65BFB1E7B39FF32F0ECF",
            "72C4CD850895D63F518C311019930CC3",
            "362286D8B72BB982443657A659F5DBEB",
            "20DE91ECB0AEEF89EFB3CA7D61603AF7A553C99984FF2858F9CC93BE0D115525",
            "F9458F81A5FF30B345DC3776A79AFAF95F74646A6908B7A8C4F6F7982A3D9015",
            "0DD42E07BBE2213F15D1120C6ECFDFE0A7833AC8AE2600169E9F901C4B08F678",
            "99AD1202BDDF485AC8B38D36CB0625833A0C105333CBC53ED0827C00CDF70BBD",
            "567166BA0A8F6CA189EFF94B39BFF708BB6A6DED340AA6106DB3CDB54BF7E59CA8317B8729C09A6A800BE3D87CDFF4E18FC995A49201F57301A50EBDE517A0D5",
            "B0AA801428C79FBF00C392E2EF2062A5472DAAFDE5C3EE9AC86C323AE0863B4454362963BFB98BA7D26B6CAB0D2BD0C0668B2DD3F6263137DF3FD2BCAF532597",
            "6FB7EECBBB801113A89B457208F86167033DB442B3D3EAEAC8A9BD381D7E4F9B31F536F66C313D325192ED0BFE1F6728C211D77A93DAA3964ECBBFAA3E8922C9",
            "8F4976EEEFEB09F5C6537F6B0D3683BA5002FC5C82750D607DFAB1707BEBE0262B959BF524116A16DCCD8CD4F1A6A2A7C04C729EA50FD8B9CA043724A6DDF3CF"
    };

    private final String[] addRoundKeyInput = {
            "202122232425262728292A2B2C2D2E2F",
            "101112131415161718191A1B1C1D1E1F",
    };

    private final String[] addRoundKeyInputKey = {
            "57C816EB3F7E12DEED2C6B56E6B5BE1A",
            "16505E6B9B3AB1E6865B77DCE082A0F4",
    };


    private final String[] addRoundKeyExpectedOutput = {
            "77E9380E64A338051556958112E3EC49",
            "2661707EAF4FC7FD9E7491F7FC9FBE13",
    };



    public final String[] mixColumnsInput = {
            "9A2B1EAC7C98DD3D914ACF1776EE891B",
            "81D13FB2BFD1F76FEA4B55427562EDE1",
            "8FDA8633ED4D5139BEA63AB28F6A9C7A",
            "249A648FE0FE9F0FE70E2C22714775DF",
            "AFC7B9578B6934E72B2692D8C57ADCDA408760450627A4A62B1A8B1F0910C918",
            "3EACA8E25CD70E178D1ECF74F01553A1685BB0DDB378A95125E2268B48E7449A",
            "2533AAA4F9E92191E41F297E1510C149414AB4A868D7274A35A7CCD8A09DA68E",
            "F0532725A22291CB4D7AD12A4AE99353A0002B5499DD546791980E96C3823CDF",
            "65FFBE6702CBD39FE6118CEDCDFD3A07C4989A22C423B7F20FAE00B582CCF99B71142BE2EE206672ECFF7DD37717FFD33B6C498AE9ED70377778929602A2A629",
            "233B0F81BF668AD47DDC51F614F147229959B9A269AD95FB7B79343FD9BB39D8C51E7C85874386BB9B2664981951B30CFFB2E550D105A49D437758A04AD6AA2E",
            "B3BCE0D926554675A8FF79F7F00C418C32288769A7B1E9E5FB6D992D76226B049689CE6B1F5B2CA70A44C41B8DA39B94A5E5A1CED93183C0C8CFA4FDAC288F7A",
            "1E8563B33883E7AAC5A843DB562635E3066A46E43A8DDE1DFB1000AFBBDEE2F372401A6299D51FF2E7ED966E712AC0EBE81BB756BD2117A30415064C4B74813D"
    };

    public final String[] mixColumnsExpectedOutput = {
            "16CEDEE8D9990F9E25B506F042D3B305",
            "32F172C7E2D2E1C93B4D13958FBCE28D",
            "044E672502E945D313F24197773D4547",
            "73EC521DA9BAF9777A44212456FE0215",
            "283D9E2711C75C40337BC7AAE17F3DDB643F5DB7B208F27EBE1346E5383E5D77",
            "265A9ED9588BDE16848906BF840AC22279F7FEFB690BA863738A0AADDB72E3D7",
            "C4F7BE3F60C4A2A584D11113A578A31D927275BB6CF91F07C662E4F2EEC9A349",
            "5387F788E1BA245006B87D90E3FD8C7F0B7AF743CFDA32633B147DF991D5F6AF",
            "541056BC458C8C5334A3511FF017BFB6DF2F9322345BACCE61AF80F6BAF0D8CF1952A486F6C1AB14155911BA99C6B321733B5D72DEB3A3887388A65FA49F4A6A",
            "94A272EE19ED0CCBA3C3E5DEE99C1EF2A4440BCB092236E30AB39099E3CFCEFC776AFB74BE1288FBF1D4775802A632F38673171ABBDE3010DC819936A88928C1",
            "595B0F13C242CEB10B980C87B0DB3DB5143C1F41EFF055F13BA630DA2E9EB7A1F8B8CEF74B328379DB953706A83B6FDA3820FDE9E779A3E172CD03D6696A4B68",
            "37964F89469782089A1E93875C5E6D0E5BECAA7102AC77B67462D4ECB6E3AD7BE8A1DA0524AE10EC322668E508CBC57960AC28C55AF76813503CFEA1DC4C19EC"
    };

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void sBox(){
        String[] input = SBoxTestCaseInput;
        String[] expectedOutput = SBoxTestCaseOutput;

        assertEquals(input.length,expectedOutput.length);
        for (int i = 0; i < input.length; i++) {
            byte[][] inputState = KalynaUtil.stringToState(input[i]);
            byte[][] val = KalynaUtil.stringToState(expectedOutput[i]);

            byte[][] out = KalynaRoundFunction.SBox(inputState);
            boolean pass = true;
            for (int j = 0; j < val.length; j++)
                pass = pass && Arrays.equals(val[j],out[j]);

            assertTrue(pass);
        }
    }

    @Test
    void invSBox() {
        String[] input = SBoxTestCaseOutput;
        String[] expectedOutput =  SBoxTestCaseInput;

        assertEquals(input.length,expectedOutput.length);
        for (int i = 0; i < input.length; i++) {
            byte[][] inputState = KalynaUtil.stringToState(input[i]);
            byte[][] val = KalynaUtil.stringToState(expectedOutput[i]);

            byte[][] out = KalynaRoundFunction.invSBox(inputState);
            boolean pass = true;
            for (int j = 0; j < val.length; j++)
                pass = pass && Arrays.equals(val[j],out[j]);

            assertTrue(pass);
        }
    }

    @Test
    void mixColumns() {
        assertEquals(mixColumnsInput.length, mixColumnsExpectedOutput.length);
        for (int state = 0; state < mixColumnsInput.length; state++) {
            byte[][] inputState = KalynaUtil.stringToState(mixColumnsInput[state]);
            byte[][] expectedOutputState = KalynaUtil.stringToState(mixColumnsExpectedOutput[state]);
            byte[][] outputState = KalynaRoundFunction.mixColumns(inputState);

            boolean pass = true;
            for (int col = 0; col < expectedOutputState.length; col++){
                pass = pass && Arrays.equals(expectedOutputState[col], outputState[col]);
            }
            assertTrue(pass);
        }
    }

    @Test
    void invMixColumns() {
        assertEquals(mixColumnsInput.length, mixColumnsExpectedOutput.length);
        for (int state = 0; state < mixColumnsInput.length; state++) {
            byte[][] inputState = KalynaUtil.stringToState(mixColumnsExpectedOutput[state]);
            byte[][] expectedOutputState = KalynaUtil.stringToState(mixColumnsInput[state]);
            byte[][] outputState = KalynaRoundFunction.invMixColumns(inputState);

            boolean pass = true;
            for (int col = 0; col < expectedOutputState.length; col++){
                pass = pass && Arrays.equals(expectedOutputState[col], outputState[col]);
            }
            assertTrue(pass);
        }
    }

    @Test
    void xorRoundKey(){
        assertEquals(xorRoundKeyInput.length,xorRoundKeyExpectedOutput.length);
        assertEquals(xorRoundKeyInput.length, xorRoundKeyInputKey.length);
        for (int state = 0; state < xorRoundKeyInput.length; state++) {
            byte[][] inputState = KalynaUtil.stringToState(xorRoundKeyInput[state]);
            byte[][] roundKeyState = KalynaUtil.stringToState(xorRoundKeyInputKey[state]);
            byte[][] expectedOutputState = KalynaUtil.stringToState(xorRoundKeyExpectedOutput[state]);
            byte[][] outputState = KalynaRoundFunction.xorRoundKey(inputState, roundKeyState);

            boolean pass = true;
            for (int col = 0; col < expectedOutputState.length; col++)
                pass = pass && Arrays.equals(expectedOutputState[col], outputState[col]);

            assertTrue(pass);
        }

    }

    @Test
    void addRoundKey(){
        assertEquals(addRoundKeyInput.length,addRoundKeyInputKey.length );
        assertEquals(addRoundKeyInput.length,addRoundKeyExpectedOutput.length );
        boolean pass = true;
        for (int i = 0; i < addRoundKeyInput.length; i++) {
            byte[][] input = KalynaUtil.stringToState(addRoundKeyInput[i]);
            byte[][] key = KalynaUtil.stringToState(addRoundKeyInputKey[i]);
            byte[][] expectedOutput = KalynaUtil.stringToState(addRoundKeyExpectedOutput[i]);

            byte[][] actualOutput = KalynaRoundFunction.addRoundKey(input,key);

            for (int j = 0; j < actualOutput.length; j++) {
                pass = pass && Arrays.equals(actualOutput[j],expectedOutput[j]);
            }
        }
        assertTrue(pass);
    }

    @Test
    void subRoundKey() {
        assertEquals(addRoundKeyInputKey.length, addRoundKeyExpectedOutput.length);
        assertEquals(addRoundKeyInputKey.length, addRoundKeyInput.length);
        for (int i = 0; i < addRoundKeyInput.length; i++) {
            byte[][] inputState = KalynaUtil.stringToState(addRoundKeyExpectedOutput[i]);
            byte[][] keyState = KalynaUtil.stringToState(addRoundKeyInputKey[i]);
            byte[][] expectedOutputState = KalynaUtil.stringToState(addRoundKeyInput[i]);

            byte[][] outputState = KalynaRoundFunction.subRoundKey(inputState, keyState);

            boolean pass = true;
            for (int j = 0; j < inputState.length; j++) {
                pass = pass && Arrays.equals(outputState[j], expectedOutputState[j]);
            }
            assertTrue(pass);
        }
    }
}