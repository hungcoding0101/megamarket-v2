import { describe, expect, it } from "vitest";
import { toSnakeCaseDeeply } from "../../../src/util/generics";

describe("toSnakeCaseDeeply", { timeout: 5000 }, () => {
  it("should return an object with all nested fields named in snake case", () => {
    const object = {
      aBcBC: {
        bCdCd: 9,
        bCdCdC: {
          cDcDc: 0
        }
      },
      aBa: 0
    };

    const expected = {
      a_bc_bc: { b_cd_cd: 9, b_cd_cd_c: { c_dc_dc: 0 } },
      a_ba: 0
    };

    expect(toSnakeCaseDeeply(object)).toEqual(expected);
  });
});
