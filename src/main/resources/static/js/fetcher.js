("use strict");

/**
 * fetcher
 */
const fetcher = {
  /**
   * get
   * @param {string} url - リクエストURL
   * @param {object} headers - ヘッダー
   * @param {object} data - リクエストボディ
   * @returns - レスポンス
   */
  async get(url, headers, data) {
    const response = await fetch(url, {
      method: "GET",
      headers: headers,
      body: JSON.stringify(data),
    });

    const text = await response.text();
    const json = text ? JSON.parse(text) : null;

    if (!response.ok) {
      const errorMessages = json.map((error) => error.defaultMessage);
      throw new Error(errorMessages.join("\n"));
    }

    return json;
  },

  /**
   * post
   * @param {string} url - リクエストURL
   * @param {object} headers - ヘッダー
   * @param {object} data - リクエストボディ
   * @returns - レスポンス
   */
  async post(url, headers, data) {
    const response = await fetch(url, {
      method: "POST",
      headers: headers,
      body: JSON.stringify(data),
    });

    const text = await response.text();
    const json = text ? JSON.parse(text) : null;

    if (!response.ok) {
      const errorMessages = json.map((error) => error.defaultMessage);
      throw new Error(errorMessages.join("\n"));
    }

    return json;
  },

  /**
   * post
   * @param {string} url - リクエストURL
   * @param {object} headers - ヘッダー
   * @param {object} data - リクエストボディ
   * @returns - レスポンス
   */
  async put(url, headers, data) {
    const response = await fetch(url, {
      method: "PUT",
      headers: headers,
      body: JSON.stringify(data),
    });

    const text = await response.text();
    const json = text ? JSON.parse(text) : null;

    if (!response.ok) {
      const errorMessages = json.map((error) => error.defaultMessage);
      throw new Error(errorMessages.join("\n"));
    }

    return json;
  },

  /**
   * get
   * @param {string} url - リクエストURL
   * @param {object} headers - ヘッダー
   * @returns - レスポンス
   */
  async delete(url, headers) {
    const response = await fetch(url, {
      method: "DELETE",
      headers: headers,
    });

    const text = await response.text();
    const json = text ? JSON.parse(text) : null;

    if (!response.ok) {
      const errorMessages = json.map((error) => error.defaultMessage);
      throw new Error(errorMessages.join("\n"));
    }

    return json;
  },
};
