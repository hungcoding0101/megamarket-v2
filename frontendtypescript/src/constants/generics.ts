import { CountryCode } from "../enums/generics";
import { PhonenumberRule } from "../types/generics";

export const PhonenumberRuleList = {
  Vietnam: <PhonenumberRule>{
    countryCode: CountryCode.VN,
    countryName: "Vietnam",
    prefix: "+84",
    pattern: /^\d{9}$/
  }
};
