console.log("Script loaded");

// change theme work
let currentTheme = getTheme();
// initial -->

document.addEventListener("DOMContentLoaded", () => {
  changeTheme();
});

// TODO:
function changeTheme() {
  // set to web page
  changePageTheme(currentTheme, currentTheme);

  // set the listener to change  theme button
  const changeThemeButton = document.querySelector("#theme_change_button");

  changeThemeButton.addEventListener("click", (event) => {
    console.log("change theme button clicked");
    let oldTheme = currentTheme;
    if (currentTheme === "dark") {
      // theme light
      currentTheme = "light";
    } else {
      // theme dark
      currentTheme = "dark";
    }

    changePageTheme(currentTheme, oldTheme);
  });
  // end of event listener
}

// set theme to localstorage
function setTheme(theme) {
  localStorage.setItem("theme", theme);
}

// get theme from localstorage
function getTheme() {
  let theme = localStorage.getItem("theme");
  return theme ? theme : "light";
}

// change current page theme
function changePageTheme(theme, oldTheme) {
  setTheme(currentTheme);

  // remove the current theme
  if (oldTheme) {
    document.querySelector("html").classList.remove(oldTheme);
  }

  // set the current theme
  document.querySelector("html").classList.add(theme);

  // change the text of button
  document
    .querySelector("#theme_change_button")
    .querySelector("span").textContent = theme == "light" ? "Dark" : "Light";
}
// end of change page theme
 