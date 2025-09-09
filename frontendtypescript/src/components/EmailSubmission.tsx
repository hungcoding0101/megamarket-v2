import { Button, Form, Row } from "antd";
import Input from "antd/es/input/Input";

function EmailSubmission({
  onEmailSubmission
}: Readonly<{
  onEmailSubmission: (value: string) => void;
}>) {
  const [form] = Form.useForm();

  return (
    <Form
      form={form}
      aria-label="emailForm"
      layout="vertical"
      onFinish={(value) => {
        onEmailSubmission(value.email);
        history.l;
      }}
    >
      <Row align={"bottom"}>
        <Form.Item
          aria-label="email"
          name="email"
          label="Email"
          rules={[
            {
              required: true
            },
            {
              type: "email"
            }
          ]}
        >
          <Input placeholder="Email" aria-label="email" />
        </Form.Item>

        <Form.Item>
          <Button type="primary" htmlType="submit" aria-label="submit">
            Submit
          </Button>
        </Form.Item>
      </Row>
    </Form>
  );
}

export default EmailSubmission;
