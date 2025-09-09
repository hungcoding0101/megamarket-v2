import { CountryCode } from "../enums/generics";

export type PhonenumberRule = {
  readonly countryCode: CountryCode;
  readonly countryName: string;
  readonly prefix: string;
  readonly pattern: RegExp;
};
