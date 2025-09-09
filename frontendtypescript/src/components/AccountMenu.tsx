import {
  LogoutOutlined,
  NotificationOutlined,
  ShoppingCartOutlined,
  ShoppingOutlined,
  UserOutlined
} from "@ant-design/icons";
import { Badge, Button, Dropdown, Menu, Modal } from "antd";
import { Link } from "react-router";

const handleMenuClick = (selectedItem) => {
  switch (selectedItem.key) {
    case "4":
      dispatch({ type: USER_LOGOUT });
      Modal.warning({ title: "You have been logged out" });
      break;

    default:
      break;
  }
};

const menu = (user) => (
  <Menu onClick={(item) => handleMenuClick(item)} theme="dark">
    <Menu.Item
      key="1"
      icon={<ShoppingOutlined />}
      hidden={user.role === "ROLE_SELLER"}
    >
      <Link to={`/customer/orders`}>Your orders</Link>
    </Menu.Item>

    <Menu.Item
      key="2"
      icon={<NotificationOutlined />}
      hidden={user.role === "ROLE_SELLER"}
    >
      <Link to={`/customer/notifications`}>
        Notifications <Badge count={user.notifications.length}></Badge>
      </Link>
    </Menu.Item>

    <Menu.Item key="3" icon={<UserOutlined />}>
      <Link to={`/customer/basic_info`}>Your account</Link>
    </Menu.Item>
    <Menu.Item key="4" icon={<LogoutOutlined />}>
      Log out
    </Menu.Item>
  </Menu>
);

function AccountMenu() {
  return (
    <Menu
      theme="dark"
      mode="horizontal"
      style={{ objectFit: "contain", height: 50 }}
      selectable={false}
      items={[
        {
          key: "cart",
          icon: (
            <Link to={"/cart"}>
              {user.role === "ROLE_CUSTOMER" ? (
                <Badge
                  count={user.cart.length}
                  offset={[0, 2]}
                  style={{ marginRight: "1rem" }}
                >
                  <Button type="primary">
                    <ShoppingCartOutlined />
                    Cart
                  </Button>
                </Badge>
              ) : null}
            </Link>
          )
        },

        {
          key: "menu",
          icon: (
            <Dropdown trigger={["click", "hover"]}>
              {menu(user)}
              <Button type="primary">
                <UserOutlined /> {user.username}
              </Button>
            </Dropdown>
          )
        }
      ]}
    ></Menu>
  );
}

export default AccountMenu;
