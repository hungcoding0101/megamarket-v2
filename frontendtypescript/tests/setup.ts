import "@testing-library/jest-dom/vitest";
import { beforeAll, vitest } from "vitest";

beforeAll(() => {
  Object.defineProperty(window, "matchMedia", {
    writable: true,
    value: vitest.fn().mockImplementation((query) => ({
      matches: false,
      media: query,
      onchange: null,
      addListener: vitest.fn(),
      removeListener: vitest.fn(),
      addEventListener: vitest.fn(),
      removeEventListener: vitest.fn(),
      dispatchEvent: vitest.fn()
    }))
  });
});
