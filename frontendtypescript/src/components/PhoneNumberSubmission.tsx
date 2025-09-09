import { Button, Form, message, Row, Select } from "antd";
import Input from "antd/es/input/Input";
import { PhonenumberRuleList } from "../constants/generics";
import { useRef } from "react";

function PhoneNumberSubmission({
  onPhoneNumberSubmission
}: Readonly<{
  onPhoneNumberSubmission: (value: string) => void;
}>) {
  const PREFIX = "prefix";
  const PHONE_NUMBER = "phoneNumber";

  const [form] = Form.useForm();

  const countryPhoneCodes = Object.values(PhonenumberRuleList).map((rule) => ({
    value: rule.prefix,
    label: `${rule.countryName} (${rule.prefix})`,
    pattern: rule.pattern
  }));

  const countryPhoneCodeSelector = (
    <Form.Item name={PREFIX} noStyle>
      <Select
        options={countryPhoneCodes}
        onSelect={(value) =>
          (pattern.current = countryPhoneCodes.find(
            (rule) => rule.value === value
          )!.pattern)
        }
      ></Select>
    </Form.Item>
  );

  const initialFormValues = { [PREFIX]: countryPhoneCodes[0].value };

  const pattern = useRef(countryPhoneCodes[0].pattern);

  return (
    <Form
      form={form}
      layout="vertical"
      initialValues={initialFormValues}
      onFinish={(value) => {
        const phoneNumber = value[PREFIX] + value[PHONE_NUMBER];
        onPhoneNumberSubmission(phoneNumber);
      }}
    >
      <Row align={"bottom"}>
        <Form.Item
          name={PHONE_NUMBER}
          label="Phone Number"
          rules={[
            {
              required: true,
              message: "Please input your phone number!"
            },
            {
              pattern: pattern.current,
              message: "Please input a valid phone number!"
            }
          ]}
        >
          <Input
            addonBefore={countryPhoneCodeSelector}
            placeholder="Phone Number"
          />
        </Form.Item>
        <Form.Item>
          <Button type="primary" htmlType="submit">
            Submit
          </Button>
        </Form.Item>
      </Row>
    </Form>
  );
}

export default PhoneNumberSubmission;
