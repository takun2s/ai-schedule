import Vue from 'vue';
import store from '@/store';

Vue.directive('permission', {
  inserted(el, binding) {
    const { value } = binding;
    const permissions = store.state.user?.permissions || [];

    if (value && !permissions.includes(value)) {
      el.parentNode?.removeChild(el);
    }
  }
});
