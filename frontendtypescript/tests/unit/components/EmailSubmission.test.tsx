import { render, screen } from "@testing-library/react";
// import { describe, it, expect, vitest } from "vitest";
import EmailSubmission from "../../../src/components/EmailSubmission";
import userEvent from "@testing-library/user-event";
import React from "react";

describe("EmailSubmission", () => {
  it("should render a form with email input and submit button", () => {
    render(<EmailSubmission onEmailSubmission={() => {}} />);
    expect(screen.getByRole("form")).toBeInTheDocument();
    expect(screen.getByRole("textbox")).toBeInTheDocument();
    expect(screen.getByRole("button")).toBeInTheDocument();
  });

  it("should run a callback with the entered email when the form is submitted", async () => {
    const onEmailSubmission = vitest.fn();
    render(<EmailSubmission onEmailSubmission={onEmailSubmission} />);
    const email = "Bt5fK@example.com";
    const input = screen.getByRole("textbox");
    const submitButton = screen.getByRole("button");

    await userEvent.type(input, email);
    await userEvent.click(submitButton);

    expect(onEmailSubmission).toHaveBeenCalledTimes(1);
    expect(onEmailSubmission).toHaveBeenCalledWith(email);
  });
});
