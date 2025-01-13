module customer {
	exports controller;
	exports service;
	exports model;
	exports repository;

	requires jakarta.persistence;
	requires spring.web;
}