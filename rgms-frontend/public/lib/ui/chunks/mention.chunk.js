(window.webpackJsonp=window.webpackJsonp||[]).push([[1],{1326:function(e,r,n){},1330:function(e,r,n){"use strict";n.r(r);var t=n(0),a=n.n(t),o=n(1),i=n.n(o),c=n(1327);n(1326);function u(e,r){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var t=Object.getOwnPropertySymbols(e);r&&(t=t.filter((function(r){return Object.getOwnPropertyDescriptor(e,r).enumerable}))),n.push.apply(n,t)}return n}function l(e){for(var r=1;r<arguments.length;r++){var n=null!=arguments[r]?arguments[r]:{};r%2?u(Object(n),!0).forEach((function(r){f(e,r,n[r])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):u(Object(n)).forEach((function(r){Object.defineProperty(e,r,Object.getOwnPropertyDescriptor(n,r))}))}return e}function f(e,r,n){return r in e?Object.defineProperty(e,r,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[r]=n,e}function s(e){return function(e){if(Array.isArray(e))return p(e)}(e)||function(e){if("undefined"!=typeof Symbol&&Symbol.iterator in Object(e))return Array.from(e)}(e)||function(e,r){if(!e)return;if("string"==typeof e)return p(e,r);var n=Object.prototype.toString.call(e).slice(8,-1);"Object"===n&&e.constructor&&(n=e.constructor.name);if("Map"===n||"Set"===n)return Array.from(e);if("Arguments"===n||/^(?:Ui|I)nt(?:8|16|32)(?:Clamped)?Array$/.test(n))return p(e,r)}(e)||function(){throw new TypeError("Invalid attempt to spread non-iterable instance.\nIn order to be iterable, non-array objects must have a [Symbol.iterator]() method.")}()}function p(e,r){(null==r||r>e.length)&&(r=e.length);for(var n=0,t=new Array(r);n<r;n++)t[n]=e[n];return t}var b={value:i.a.string,placeholder:i.a.string,onChange:i.a.func.isRequired,onBlur:i.a.func,onFocus:i.a.func,onKeyDown:i.a.func,userData:i.a.arrayOf(i.a.object),"aria-label":i.a.string},y=a.a.forwardRef((function(e,r){var n=e.value,t=void 0===n?"":n,o=e.onChange,i=e.onKeyDown,u=e.onBlur,f=e.onFocus,p=e.placeholder,b=e.userData,y=e["aria-label"],m=s(b.map((function(e){return l(l({},e),{},{display:e.value})})));return a.a.createElement("div",{className:"mention-element",onMouseDown:function(e){return e.stopPropagation()}},a.a.createElement(c.b,{className:"mention",inputRef:r,value:t,onChange:o,onKeyDown:i,onBlur:u,onFocus:f,placeholder:p,"aria-label":y,allowSpaceInQuery:!0},a.a.createElement(c.a,{trigger:"@",data:m,displayTransform:function(e,r){return"@".concat(r)},renderSuggestion:function(e,r,n){return a.a.createElement(a.a.Fragment,null,n,a.a.createElement("div",{className:"email"},e.email))}})))}));y.propTypes=b;var m=y;r.default=m}}]);