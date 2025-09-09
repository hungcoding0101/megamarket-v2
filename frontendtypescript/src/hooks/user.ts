import { useQuery } from "@tanstack/react-query";
import { getUserKey } from "../querykeys/user";
import axios from "axios";
import { User } from "../types/auth";
import _ from "lodash";

export const useUserInfo = () => {
  return useQuery({
    queryKey: getUserKey(),
    queryFn: () => {
      axios
        .get("/user/info")
        .then((res) => {
          return _.mapKeys(res.data, (value, key) => _.camelCase(key));
        })
        .catch((err) => {
          console.log(err);
        });
    }
  });
};
