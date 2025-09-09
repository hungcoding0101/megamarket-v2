import { UserOutlined } from "@ant-design/icons";
import { Button, Col, Row } from "antd";
import { Link, Outlet } from "react-router";
import AccountMenu from "../components/AccountMenu";
import { useSelector } from "react-redux";
import { RootState } from "../store";

const SignInButton = () => (
  <Link
    to={{
      pathname: "/signin"
    }}
  >
    <Button type="primary" icon={<UserOutlined></UserOutlined>}>
      Sign in
    </Button>
  </Link>
);

const RegistrationButton = () => (
  <Link
    to={{
      pathname: "/register"
    }}
  >
    <Button type="primary" icon={<UserOutlined></UserOutlined>}>
      Register
    </Button>
  </Link>
);

function RootLayout() {
  const authenticationState = useSelector((state: RootState) => state.auth);

  return (
    <div>
      <header>
        <Row justify="space-between">
          <Col span={6}>
            <div>
              <Link className="brand" to={"/"}>
                MegaMarket
              </Link>
            </div>
          </Col>

          <Col xs={2} sm={2} md={8} lg={8} xl={8} xxl={8}>
            <div>
              {authenticationState.isLoggedIn === true ? (
                AccountMenu()
              ) : (
                <div>
                  {SignInButton()}
                  {RegistrationButton()}
                </div>
              )}
            </div>
          </Col>
        </Row>
      </header>
      <main>
        <Outlet />
      </main>
    </div>
  );
}

export default RootLayout;
