#!/usr/bin/env python3
import requests
import json
import urllib.parse

BASE_URL = "http://localhost:8080/api/v1/search/autocomplete"

def main():
    print("자동완성 테스트 클라이언트")
    print("검색어 입력 후 엔터를 누르면 요청합니다. 종료: q 또는 빈 엔터\n")

    while True:
        keyword = input("검색어 입력: ").strip()

        # 종료 조건
        if keyword == "" or keyword.lower() == "q":
            print("종료합니다.")
            break

        # URL 인코딩
        encoded_keyword = urllib.parse.quote(keyword)
        url = f"{BASE_URL}/{encoded_keyword}"

        print(f"\n[요청] GET {url}")

        try:
            resp = requests.get(url, timeout=3)
        except requests.RequestException as e:
            print(f"[ERROR] 요청 실패: {e}")
            continue

        print(f"[응답 코드] {resp.status_code}")

        content_type = resp.headers.get("Content-Type", "")

        # JSON 응답이면 예쁘게 출력
        if "application/json" in content_type:
            try:
                data = resp.json()
            except json.JSONDecodeError:
                print("[WARN] JSON 디코딩 실패. 원본 응답:")
                print(resp.text)
                print()
                continue

            print("\n[응답 JSON]")
            print(json.dumps(data, ensure_ascii=False, indent=2))

            # 리스트라면 name만 따로 출력
            if isinstance(data, list):
                print("\n[자동완성 결과]")
                for i, item in enumerate(data, start=1):
                    if isinstance(item, dict):
                        print(f"{i}. {item.get('name')}")
            print()

        else:
            # JSON 아닐 경우 그냥 텍스트 출력
            print("[응답 내용]")
            print(resp.text)
            print()


if __name__ == "__main__":
    main()
