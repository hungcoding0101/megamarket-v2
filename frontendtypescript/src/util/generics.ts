import camelcaseKeysDeep from "camelcase-keys-deep";
import snakecaseKeys from "snakecase-keys";

export const toSnakeCaseDeeply = (object: object) => {
  return snakecaseKeys(object as Record<string, unknown>);
};

export const toCamelCaseDeeply = (object: object) => {
  return camelcaseKeysDeep(object);
};
