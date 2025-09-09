import { Button, Form, Input } from "antd";

function PasswordSubmission({
  onPasswordSubmission
}: {
  onPasswordSubmission: (value: string) => void;
}) {
  const [form] = Form.useForm();

  const PASSWORD = "password";

  return (
    <Form
      form={form}
      layout="vertical"
      onFinish={(value) => {
        onPasswordSubmission(value[PASSWORD]);
      }}
    >
      <Form.Item
        name={PASSWORD}
        label="Password"
        rules={[
          {
            required: true,
            message: "Please input your password!"
          }
        ]}
        hasFeedback
      >
        <Input.Password />
      </Form.Item>

      <Form.Item
        name="confirm"
        label="Confirm Password"
        dependencies={["password"]}
        hasFeedback
        rules={[
          {
            required: true,
            message: "Please confirm your password!"
          },
          ({ getFieldValue }) => ({
            validator(_, value) {
              if (!value || getFieldValue(PASSWORD) === value) {
                return Promise.resolve();
              }
              return Promise.reject(
                new Error("The new password that you entered do not match!")
              );
            }
          })
        ]}
      >
        <Input.Password />
      </Form.Item>
      <Form.Item>
        <Button type="primary" htmlType="submit">
          Submit
        </Button>
      </Form.Item>
    </Form>
  );
}

export default PasswordSubmission;
