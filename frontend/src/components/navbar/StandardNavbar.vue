<template>
  <b-navbar class="justify-content-between shadow-sm" id="bar-style">
    <b-navbar-brand class="ms-2" :to="{ path: '/' }" style="cursor: pointer">Archedetector</b-navbar-brand>
    <b-collapse id="nav-collapse" is-nav>
      <b-navbar-nav>
        <b-nav-item :to="{ path: '/' }">Home</b-nav-item>
        <b-nav-item :to="{ path: '/manage/query-collection' }">Manage</b-nav-item>
      </b-navbar-nav>
    </b-collapse>
    <img class="img-responsive"
         src="@/assets/logo-groningen.svg"
         alt="" />
  </b-navbar>
</template>

<script>
export default {
  name: "standardNavbar",
}
</script>

<style scoped>
#bar-style{
  height:70px

}
</style>