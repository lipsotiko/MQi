export const distinct = (arr) => {
  var u = {}, a = [];
  for (var i = 0, l = arr.length; i < l; ++i) {
    if (!u.hasOwnProperty(arr[i])) {
      a.push(arr[i]);
      u[arr[i]] = 1;
    }
  }

  return a;
}

export const ramdomInt = () => {
  return Math.floor(Math.random() * 1000) + 1;
}

export const compare = (a, b) => {
  if (a.measureName < b.measureName)
    return -1;
  if (a.measureName > b.measureName)
    return 1;
  return 0;
}

export const headers = {
  "Content-Type": "application/json"
};

export function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}