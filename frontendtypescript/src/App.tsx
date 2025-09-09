import "./App.css";
import { RouterProvider } from "react-router";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { rootRouter } from "./routes/root";

const queryClient = new QueryClient();

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <RouterProvider router={rootRouter}></RouterProvider>
    </QueryClientProvider>
  );
}

export default App;
