import argparse
import json
import jwt
from urllib import request, parse, error

def main():
    parser = argparse.ArgumentParser(description="Seed JWT tokens for performance testing.")
    parser.add_argument("--host", default="http://localhost:8081", help="The host URL (default: http://localhost:8081)")
    args = parser.parse_args()

    full_tokens = {}
    host = args.host.rstrip("/")
    login_path = f"{host}/login"

    for i in range(1, 501):
        username_input = f"user_{i}"
        data = {"username": username_input, "password": "password"}

        encoded_data = parse.urlencode(data).encode()

        req = request.Request(
            login_path,
            data=encoded_data,
            method="POST",
            headers={"Content-Type": "application/x-www-form-urlencoded"},
        )

        try:
            with request.urlopen(req, timeout=5) as response:
                body = response.read().decode()

                try:
                    parsed = json.loads(body)
                except json.JSONDecodeError:
                    print(f"[{username_input}] ❌ Invalid JSON: {body}")
                    continue

                try:
                    token = parsed["accessToken"]["accessToken"]
                    username = parsed["profile"]["username"]
                    full_tokens[username] = {
                        "token": token,
                        "profile": parsed["profile"]
                    }
                except KeyError as e:
                    print(f"[{username_input}] ❌ Missing field {e}: {parsed}")

        except error.HTTPError as e:
            error_body = e.read().decode()
            print(f"[{username_input}] ❌ HTTP {e.code}: {error_body}")

        except error.URLError as e:
            print(f"[{username_input}] ❌ Network error: {e.reason}")

        except Exception as e:
            print(f"[{username_input}] ❌ Unexpected error: {e}")

    with open("jwts.json", "w") as f:
        json.dump(full_tokens, f)

if __name__ == "__main__":
    main()
