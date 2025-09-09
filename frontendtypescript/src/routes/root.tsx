import {
  createBrowserRouter,
  createRoutesFromElements,
  Route
} from "react-router";
import RootLayout from "../layouts/RootLayout";
import Home from "../pages/Home";
import Registration from "../pages/Registration";

export const rootRouter = createBrowserRouter(
  createRoutesFromElements(
    <Route path="/" element={<RootLayout />}>
      <Route index element={<Home></Home>}></Route>
      <Route path="/register" element={<Registration></Registration>}></Route>
    </Route>
  )
);
